package com.tzesh.springtemplate.config.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzesh.springtemplate.base.response.BaseResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/**
 * Global IP-based rate limiting filter.
 * Acts as a baseline safety net. Can be disabled via configuration.
 * Uses Redis for distributed rate limiting with key prefix "rate_limit:global:".
 *
 * @author tzesh
 */
@Component
@Slf4j
public class RateLimitingFilter implements Filter {
    private final int limit;
    private final Duration duration;
    private final boolean enabled;
    final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(
            StringRedisTemplate redisTemplate,
            RateLimitProperties properties,
            ObjectMapper objectMapper
    ) {
        this.limit = properties.getLimit();
        this.duration = Duration.ofMinutes(properties.getDurationMinutes());
        this.enabled = properties.isEnabled();
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private boolean isAllowed(String key) {
        try {
            String redisKey = "rate_limit:global:" + key;
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1) {
                redisTemplate.expire(redisKey, duration);
            }
            return count != null && count <= limit;
        } catch (Exception e) {
            log.warn("Redis unavailable for global rate limiting, failing open: {}", e.getMessage());
            return true;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        if (request instanceof HttpServletRequest req && response instanceof HttpServletResponse res) {
            String ip = req.getRemoteAddr();
            if (isAllowed(ip)) {
                chain.doFilter(request, response);
            } else {
                res.setStatus(429);
                res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                BaseResponse<String> errorResponse = BaseResponse.tooManyRequests("Rate limit exceeded. Please try again later.")
                        .message("Too Many Requests");
                res.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
