package com.tzesh.springtemplate.config.ratelimit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for rate limiting
 * @author tzesh
 */
@Data
@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {
    private boolean enabled = true;
    private int limit = 100;
    private long durationMinutes = 1;

    private CategoryConfig strict = new CategoryConfig(10, 1);
    private CategoryConfig standard = new CategoryConfig(60, 1);
    private CategoryConfig relaxed = new CategoryConfig(200, 1);
    private CategoryConfig authentication = new CategoryConfig(5, 1);

    @Data
    public static class CategoryConfig {
        private int limit;
        private long durationMinutes;

        public CategoryConfig() {
        }

        public CategoryConfig(int limit, long durationMinutes) {
            this.limit = limit;
            this.durationMinutes = durationMinutes;
        }
    }
}
