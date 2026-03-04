package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.RateLimitErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotencyExceptionTest {
    @Test
    @DisplayName("Should instantiate IdempotencyException with IDEMPOTENCY_KEY_MISSING message")
    void idempotencyException_instantiatesWithKeyMissing() {
        IdempotencyException ex = new IdempotencyException(RateLimitErrorMessage.IDEMPOTENCY_KEY_MISSING);
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorMessage()).isEqualTo(RateLimitErrorMessage.IDEMPOTENCY_KEY_MISSING);
        assertThat(ex.getErrorMessage().getMessage()).isEqualTo("Idempotency-Key header is required for this request.");
    }

    @Test
    @DisplayName("Should instantiate IdempotencyException with IDEMPOTENCY_KEY_IN_PROGRESS message")
    void idempotencyException_instantiatesWithKeyInProgress() {
        IdempotencyException ex = new IdempotencyException(RateLimitErrorMessage.IDEMPOTENCY_KEY_IN_PROGRESS);
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorMessage()).isEqualTo(RateLimitErrorMessage.IDEMPOTENCY_KEY_IN_PROGRESS);
        assertThat(ex.getErrorMessage().getMessage()).isEqualTo("A request with this idempotency key is already being processed.");
    }
}
