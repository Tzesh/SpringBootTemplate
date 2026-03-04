package com.tzesh.springtemplate.base.annotation;

/**
 * Strategy for determining the rate limit key
 * @author tzesh
 */
public enum RateLimitKeyStrategy {
    IP,
    USER,
    IP_AND_USER,
    CUSTOM
}
