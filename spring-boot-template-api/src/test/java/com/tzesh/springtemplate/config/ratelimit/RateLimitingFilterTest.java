package com.tzesh.springtemplate.config.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.mockito.Mockito.*;

class RateLimitingFilterTest {
    private RateLimitingFilter filter;

    @BeforeEach
    void setUp() {
        // Use a mock StringRedisTemplate for Redis-based rate limiting
        org.springframework.data.redis.core.StringRedisTemplate redisTemplate = mock(org.springframework.data.redis.core.StringRedisTemplate.class);
        filter = new RateLimitingFilter(redisTemplate, 2, 1L); // 2 requests per minute
        // Optionally, you can mock redisTemplate behavior for more advanced tests
    }

    @Test
    void allowsRequestsWithinLimit() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        // Simulate Redis increments
        org.springframework.data.redis.core.ValueOperations valueOps = mock(org.springframework.data.redis.core.ValueOperations.class);
        when(filter.redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(anyString())).thenReturn(1L, 2L);
        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain);
        verify(chain, times(2)).doFilter(request, response);
    }

    @Test
    void blocksRequestsOverLimit() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(response.getWriter()).thenReturn(mock(java.io.PrintWriter.class));
        org.springframework.data.redis.core.ValueOperations valueOps = mock(org.springframework.data.redis.core.ValueOperations.class);
        when(filter.redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(anyString())).thenReturn(1L, 2L, 3L);
        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain); // 3rd should be blocked
        verify(chain, times(2)).doFilter(request, response);
        verify(response, times(1)).setStatus(429);
    }
}
