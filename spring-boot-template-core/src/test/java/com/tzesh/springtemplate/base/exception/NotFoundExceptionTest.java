package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class NotFoundExceptionTest {
    @Test
    @DisplayName("Should instantiate NotFoundException with correct error message")
    void notFoundException_instantiatesWithErrorMessage() {
        // Arrange: create a GenericErrorMessage
        GenericErrorMessage error = GenericErrorMessage.builder().message("Not found").build();
        // Act: instantiate NotFoundException
        NotFoundException ex = new NotFoundException(error);
        // Assert: verify error message is set
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorMessage()).isEqualTo(error);
    }
}

