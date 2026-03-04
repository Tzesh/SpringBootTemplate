package com.tzesh.springtemplate.config.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzesh.springtemplate.base.annotation.Idempotent;
import com.tzesh.springtemplate.base.exception.IdempotencyException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class IdempotencyAspectTest {
    private IdempotencyAspect aspect;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;
    private ObjectMapper objectMapper;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        objectMapper = new ObjectMapper();
        aspect = new IdempotencyAspect(redisTemplate, objectMapper);
    }

    @Test
    @DisplayName("Should throw IdempotencyException when idempotency key header is missing")
    void handleIdempotent_missingKey_throwsException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Idempotent idempotent = createIdempotent(24, TimeUnit.HOURS, "Idempotency-Key");

        assertThatThrownBy(() -> aspect.handleIdempotent(joinPoint, idempotent))
                .isInstanceOf(IdempotencyException.class);
    }

    @Test
    @DisplayName("Should throw IdempotencyException when request is already in progress")
    void handleIdempotent_inProgress_throwsException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Idempotency-Key", "test-key-123");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(valueOps.get("idempotency:test-key-123")).thenReturn(null);
        when(valueOps.setIfAbsent(eq("idempotency_lock:test-key-123"), eq("locked"), any(Duration.class))).thenReturn(false);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Idempotent idempotent = createIdempotent(24, TimeUnit.HOURS, "Idempotency-Key");

        assertThatThrownBy(() -> aspect.handleIdempotent(joinPoint, idempotent))
                .isInstanceOf(IdempotencyException.class);
    }

    @Test
    @DisplayName("Should process request and cache response for new idempotency key")
    void handleIdempotent_newKey_processesAndCaches() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Idempotency-Key", "new-key-456");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(valueOps.get("idempotency:new-key-456")).thenReturn(null);
        when(valueOps.setIfAbsent(eq("idempotency_lock:new-key-456"), eq("locked"), any(Duration.class))).thenReturn(true);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("result");

        Idempotent idempotent = createIdempotent(24, TimeUnit.HOURS, "Idempotency-Key");
        Object result = aspect.handleIdempotent(joinPoint, idempotent);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
        verify(redisTemplate).delete("idempotency_lock:new-key-456");
    }

    private Idempotent createIdempotent(long ttl, TimeUnit timeUnit, String headerName) {
        return new Idempotent() {
            @Override public Class<? extends Annotation> annotationType() { return Idempotent.class; }
            @Override public long ttl() { return ttl; }
            @Override public TimeUnit timeUnit() { return timeUnit; }
            @Override public String headerName() { return headerName; }
        };
    }
}
