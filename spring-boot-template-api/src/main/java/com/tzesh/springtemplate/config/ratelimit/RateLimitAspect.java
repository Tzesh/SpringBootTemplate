package com.tzesh.springtemplate.config.ratelimit;

import com.tzesh.springtemplate.base.annotation.RateLimit;
import com.tzesh.springtemplate.base.annotation.RateLimitCategory;
import com.tzesh.springtemplate.base.annotation.RateLimitCategoryType;
import com.tzesh.springtemplate.base.annotation.RateLimitKeyStrategy;
import com.tzesh.springtemplate.base.error.RateLimitErrorMessage;
import com.tzesh.springtemplate.base.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * AOP aspect for handling {@link RateLimit} and {@link RateLimitCategory} annotations.
 * Uses Redis for distributed rate limiting.
 * {@link RateLimit} takes precedence over {@link RateLimitCategory}.
 * Fails open if Redis is unavailable.
 *
 * @author tzesh
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {
    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final ParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();

    @Around("@annotation(rateLimit)")
    public Object handleRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String methodKey = getMethodKey(joinPoint);
        String clientKey = resolveKey(rateLimit.key(), rateLimit.keyExpression(), joinPoint);
        String redisKey = "rate_limit:" + methodKey + ":" + clientKey;
        Duration duration = Duration.of(rateLimit.duration(), rateLimit.timeUnit().toChronoUnit());

        checkRateLimit(redisKey, rateLimit.limit(), duration);
        return joinPoint.proceed();
    }

    @Around("@within(rateLimitCategory) && !@annotation(com.tzesh.springtemplate.base.annotation.RateLimit) && !@annotation(com.tzesh.springtemplate.base.annotation.RateLimitCategory)")
    public Object handleClassLevelCategory(ProceedingJoinPoint joinPoint, RateLimitCategory rateLimitCategory) throws Throwable {
        return applyCategory(joinPoint, rateLimitCategory);
    }

    @Around("@annotation(rateLimitCategory) && !@annotation(com.tzesh.springtemplate.base.annotation.RateLimit)")
    public Object handleMethodLevelCategory(ProceedingJoinPoint joinPoint, RateLimitCategory rateLimitCategory) throws Throwable {
        return applyCategory(joinPoint, rateLimitCategory);
    }

    private Object applyCategory(ProceedingJoinPoint joinPoint, RateLimitCategory rateLimitCategory) throws Throwable {
        RateLimitCategoryType type = rateLimitCategory.value();
        RateLimitProperties.CategoryConfig config = getCategoryConfig(type);
        String methodKey = getMethodKey(joinPoint);
        String clientKey = resolveKey(rateLimitCategory.key(), "", joinPoint);
        String redisKey = "rate_limit:" + methodKey + ":" + clientKey;
        Duration duration = Duration.ofMinutes(config.getDurationMinutes());

        checkRateLimit(redisKey, config.getLimit(), duration);
        return joinPoint.proceed();
    }

    private void checkRateLimit(String redisKey, int limit, Duration duration) {
        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1) {
                redisTemplate.expire(redisKey, duration);
            }
            if (count != null && count > limit) {
                throw new RateLimitExceededException(RateLimitErrorMessage.RATE_LIMIT_EXCEEDED);
            }
        } catch (RateLimitExceededException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Redis unavailable for rate limiting, failing open: {}", e.getMessage());
        }
    }

    private String resolveKey(RateLimitKeyStrategy strategy, String keyExpression, ProceedingJoinPoint joinPoint) {
        return switch (strategy) {
            case IP -> getClientIp();
            case USER -> getCurrentUsername();
            case IP_AND_USER -> getClientIp() + ":" + getCurrentUsername();
            case CUSTOM -> evaluateSpelExpression(keyExpression, joinPoint);
        };
    }

    private String getClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isEmpty()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return "unknown";
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }

    private String evaluateSpelExpression(String expression, ProceedingJoinPoint joinPoint) {
        if (expression == null || expression.isEmpty()) {
            return getClientIp();
        }
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                    joinPoint.getTarget(), method, joinPoint.getArgs(), DISCOVERER
            );
            return PARSER.parseExpression(expression).getValue(context, String.class);
        } catch (Exception e) {
            log.warn("Failed to evaluate SpEL expression '{}', falling back to IP: {}", expression, e.getMessage());
            return getClientIp();
        }
    }

    private String getMethodKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getName();
    }

    private RateLimitProperties.CategoryConfig getCategoryConfig(RateLimitCategoryType type) {
        return switch (type) {
            case STRICT -> properties.getStrict();
            case STANDARD -> properties.getStandard();
            case RELAXED -> properties.getRelaxed();
            case AUTHENTICATION -> properties.getAuthentication();
        };
    }
}
