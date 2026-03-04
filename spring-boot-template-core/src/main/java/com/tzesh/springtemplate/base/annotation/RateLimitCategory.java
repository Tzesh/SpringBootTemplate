package com.tzesh.springtemplate.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for category-based rate limiting using predefined tiers.
 * Can be applied at class level (applies to all methods) or method level (overrides class-level).
 * {@link RateLimit} takes precedence over this annotation when both are present on a method.
 *
 * @author tzesh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RateLimitCategory {
    RateLimitCategoryType value();

    RateLimitKeyStrategy key() default RateLimitKeyStrategy.IP;
}
