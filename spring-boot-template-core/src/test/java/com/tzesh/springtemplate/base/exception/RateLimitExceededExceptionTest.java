package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.RateLimitErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitExceededExceptionTest {
    @Test
    @DisplayName("Should instantiate RateLimitExceededException with correct error message")
    void rateLimitExceededException_instantiatesWithErrorMessage() {
        RateLimitExceededException ex = new RateLimitExceededException(RateLimitErrorMessage.RATE_LIMIT_EXCEEDED);
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorMessage()).isEqualTo(RateLimitErrorMessage.RATE_LIMIT_EXCEEDED);
        assertThat(ex.getErrorMessage().getMessage()).isEqualTo("Rate limit exceeded. Please try again later.");
    }
}
