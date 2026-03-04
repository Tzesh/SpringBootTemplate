package com.tzesh.springtemplate.config.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class RateLimitingFilterTest {
    private RateLimitingFilter filter;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOps;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        RateLimitProperties properties = new RateLimitProperties();
        properties.setEnabled(true);
        properties.setLimit(2);
        properties.setDurationMinutes(1);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        filter = new RateLimitingFilter(redisTemplate, properties, objectMapper);
    }

    @Test
    @DisplayName("Should allow requests within limit")
    void allowsRequestsWithinLimit() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(valueOps.increment(anyString())).thenReturn(1L, 2L);

        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain);

        verify(chain, times(2)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should block requests over limit with JSON 429 response")
    void blocksRequestsOverLimit() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(response.getWriter()).thenReturn(writer);
        when(valueOps.increment(anyString())).thenReturn(1L, 2L, 3L);

        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain);
        filter.doFilter(request, response, chain); // 3rd should be blocked

        verify(chain, times(2)).doFilter(request, response);
        verify(response, times(1)).setStatus(429);
        verify(response, times(1)).setContentType("application/json");
        verify(writer, times(1)).write(anyString());
    }

    @Test
    @DisplayName("Should pass through when filter is disabled")
    void passesThrough_whenDisabled() throws IOException, ServletException {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setEnabled(false);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        RateLimitingFilter disabledFilter = new RateLimitingFilter(redisTemplate, properties, mapper);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        disabledFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(valueOps);
    }

    @Test
    @DisplayName("Should fail open when Redis is unavailable")
    void failsOpen_whenRedisUnavailable() throws IOException, ServletException {
        when(valueOps.increment(anyString())).thenThrow(new RuntimeException("Redis connection refused"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
