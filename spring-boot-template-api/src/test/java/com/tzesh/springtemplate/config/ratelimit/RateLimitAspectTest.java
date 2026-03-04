package com.tzesh.springtemplate.config.ratelimit;

import com.tzesh.springtemplate.base.annotation.RateLimit;
import com.tzesh.springtemplate.base.annotation.RateLimitKeyStrategy;
import com.tzesh.springtemplate.base.exception.RateLimitExceededException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RateLimitAspectTest {
    private RateLimitAspect aspect;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        RateLimitProperties properties = new RateLimitProperties();
        aspect = new RateLimitAspect(redisTemplate, properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("Should allow request within rate limit")
    void handleRateLimit_withinLimit_proceeds() throws Throwable {
        when(valueOps.increment(anyString())).thenReturn(1L);

        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenReturn("success");

        RateLimit rateLimit = createRateLimit(10, 1, TimeUnit.MINUTES, RateLimitKeyStrategy.IP, "");
        Object result = aspect.handleRateLimit(joinPoint, rateLimit);

        assertThat(result).isEqualTo("success");
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Should block request exceeding rate limit")
    void handleRateLimit_exceedsLimit_throwsException() {
        when(valueOps.increment(anyString())).thenReturn(11L);

        ProceedingJoinPoint joinPoint = mockJoinPoint();
        RateLimit rateLimit = createRateLimit(10, 1, TimeUnit.MINUTES, RateLimitKeyStrategy.IP, "");

        assertThatThrownBy(() -> aspect.handleRateLimit(joinPoint, rateLimit))
                .isInstanceOf(RateLimitExceededException.class);
    }

    @Test
    @DisplayName("Should fail open when Redis is unavailable")
    void handleRateLimit_redisDown_failsOpen() throws Throwable {
        when(valueOps.increment(anyString())).thenThrow(new RuntimeException("Redis connection refused"));

        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenReturn("success");

        RateLimit rateLimit = createRateLimit(10, 1, TimeUnit.MINUTES, RateLimitKeyStrategy.IP, "");
        Object result = aspect.handleRateLimit(joinPoint, rateLimit);

        assertThat(result).isEqualTo("success");
    }

    private ProceedingJoinPoint mockJoinPoint() {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getDeclaringTypeName()).thenReturn("TestController");
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getSignature()).thenReturn(signature);
        return joinPoint;
    }

    private RateLimit createRateLimit(int limit, int duration, TimeUnit timeUnit, RateLimitKeyStrategy key, String keyExpression) {
        return new RateLimit() {
            @Override public Class<? extends Annotation> annotationType() { return RateLimit.class; }
            @Override public int limit() { return limit; }
            @Override public int duration() { return duration; }
            @Override public TimeUnit timeUnit() { return timeUnit; }
            @Override public RateLimitKeyStrategy key() { return key; }
            @Override public String keyExpression() { return keyExpression; }
        };
    }
}
