package com.tzesh.springtemplate.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation for idempotent endpoint handling.
 * Prevents duplicate request processing using an idempotency key header.
 * Requires Redis for distributed locking and response caching.
 *
 * @author tzesh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Idempotent {
    long ttl() default 24;

    TimeUnit timeUnit() default TimeUnit.HOURS;

    String headerName() default "Idempotency-Key";
}
