package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OperationFailedExceptionTest {
    @Test
    @DisplayName("Should instantiate OperationFailedException with correct error message")
    void operationFailedException_instantiatesWithErrorMessage() {
        // Arrange: create a GenericErrorMessage
        GenericErrorMessage error = GenericErrorMessage.builder().message("Operation failed").build();
        // Act: instantiate OperationFailedException
        OperationFailedException ex = new OperationFailedException(error);
        // Assert: verify error message is set
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorMessage()).isEqualTo(error);
    }
}

