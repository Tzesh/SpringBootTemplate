package com.tzesh.springtemplate.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation for custom per-endpoint rate limiting.
 * Takes precedence over {@link RateLimitCategory} when both are present.
 *
 * @author tzesh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    int limit() default 60;

    int duration() default 1;

    TimeUnit timeUnit() default TimeUnit.MINUTES;

    RateLimitKeyStrategy key() default RateLimitKeyStrategy.IP;

    String keyExpression() default "";
}
