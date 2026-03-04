package com.tzesh.springtemplate.base.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error messages for rate limiting and idempotency
 * @author tzesh
 */
@Getter
@RequiredArgsConstructor
public enum RateLimitErrorMessage implements BaseErrorMessage {
    RATE_LIMIT_EXCEEDED("Rate limit exceeded. Please try again later."),
    IDEMPOTENCY_KEY_MISSING("Idempotency-Key header is required for this request."),
    IDEMPOTENCY_KEY_IN_PROGRESS("A request with this idempotency key is already being processed.");

    private final String message;
}
