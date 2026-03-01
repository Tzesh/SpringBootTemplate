package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UnauthorizedExceptionTest {
    @Test
    @DisplayName("Should instantiate UnauthorizedException with correct error message")
    void unauthorizedException_instantiatesWithErrorMessage() {
        // Arrange: create a GenericErrorMessage
        GenericErrorMessage error = GenericErrorMessage.builder().message("Unauthorized").build();
        // Act: instantiate UnauthorizedException
        UnauthorizedException ex = new UnauthorizedException(error);
        // Assert: verify error message is set
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorMessage()).isEqualTo(error);
    }
}

