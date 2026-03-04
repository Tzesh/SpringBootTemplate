package com.tzesh.springtemplate.base.annotation;

/**
 * Predefined rate limit category types with default limits
 * @author tzesh
 */
public enum RateLimitCategoryType {
    STRICT(10, 1),
    STANDARD(60, 1),
    RELAXED(200, 1),
    AUTHENTICATION(5, 1);

    private final int limit;
    private final int durationMinutes;

    RateLimitCategoryType(int limit, int durationMinutes) {
        this.limit = limit;
        this.durationMinutes = durationMinutes;
    }

    public int getLimit() {
        return limit;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
