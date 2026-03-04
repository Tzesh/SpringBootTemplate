package com.tzesh.springtemplate.config.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzesh.springtemplate.base.annotation.Idempotent;
import com.tzesh.springtemplate.base.error.RateLimitErrorMessage;
import com.tzesh.springtemplate.base.exception.IdempotencyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * AOP aspect for handling {@link Idempotent} annotation.
 * Uses Redis for distributed locking and response caching.
 * Fails closed if Redis is unavailable (throws exception rather than allowing duplicate processing).
 *
 * @author tzesh
 */
@Aspect
@Component
@Slf4j
public class IdempotencyAspect {
    private static final String KEY_PREFIX = "idempotency:";
    private static final String LOCK_PREFIX = "idempotency_lock:";
    private static final long LOCK_TTL_SECONDS = 30;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public IdempotencyAspect(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(idempotent)")
    public Object handleIdempotent(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        String idempotencyKey = getIdempotencyKey(idempotent.headerName());
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new IdempotencyException(RateLimitErrorMessage.IDEMPOTENCY_KEY_MISSING);
        }

        String cacheKey = KEY_PREFIX + idempotencyKey;
        String lockKey = LOCK_PREFIX + idempotencyKey;
        Duration ttl = Duration.of(idempotent.ttl(), idempotent.timeUnit().toChronoUnit());

        // Check if we already have a cached response
        String cachedResponse = redisTemplate.opsForValue().get(cacheKey);
        if (cachedResponse != null) {
            return deserializeResponse(cachedResponse);
        }

        // Try to acquire distributed lock
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(LOCK_TTL_SECONDS));
        if (lockAcquired == null || !lockAcquired) {
            throw new IdempotencyException(RateLimitErrorMessage.IDEMPOTENCY_KEY_IN_PROGRESS);
        }

        try {
            // Double-check after acquiring lock
            cachedResponse = redisTemplate.opsForValue().get(cacheKey);
            if (cachedResponse != null) {
                return deserializeResponse(cachedResponse);
            }

            // Execute the actual method
            Object result = joinPoint.proceed();

            // Cache the response
            if (result instanceof ResponseEntity<?> responseEntity) {
                CachedIdempotentResponse cached = new CachedIdempotentResponse(
                        responseEntity.getStatusCode().value(),
                        objectMapper.writeValueAsString(responseEntity.getBody())
                );
                redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(cached), ttl);
            }

            return result;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private String getIdempotencyKey(String headerName) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getHeader(headerName);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> deserializeResponse(String json) {
        try {
            CachedIdempotentResponse cached = objectMapper.readValue(json, CachedIdempotentResponse.class);
            Object body = objectMapper.readValue(cached.getBody(), Object.class);
            return new ResponseEntity<>(body, HttpStatus.valueOf(cached.getStatusCode()));
        } catch (Exception e) {
            log.error("Failed to deserialize cached idempotent response: {}", e.getMessage());
            throw new IdempotencyException(RateLimitErrorMessage.IDEMPOTENCY_KEY_IN_PROGRESS);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CachedIdempotentResponse implements Serializable {
        private int statusCode;
        private String body;
    }
}
