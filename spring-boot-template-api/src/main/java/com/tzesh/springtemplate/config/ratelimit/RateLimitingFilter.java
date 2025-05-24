package com.tzesh.springtemplate.config.ratelimit;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter implements Filter {
    private final int limit;
    private final Duration duration;
    final StringRedisTemplate redisTemplate;

    public RateLimitingFilter(
            StringRedisTemplate redisTemplate,
            @Value("${ratelimit.limit:100}") int limit,
            @Value("${ratelimit.duration-minutes:1}") long durationMinutes
    ) {
        this.limit = limit;
        this.duration = Duration.ofMinutes(durationMinutes);
        this.redisTemplate = redisTemplate;
    }

    private boolean isAllowed(String key) {
        String redisKey = "rate_limit:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, duration);
        }
        return count != null && count <= limit;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest req && response instanceof HttpServletResponse res) {
            String ip = req.getRemoteAddr();
            if (isAllowed(ip)) {
                chain.doFilter(request, response);
            } else {
                res.setStatus(429);
                res.getWriter().write("Too Many Requests");
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
