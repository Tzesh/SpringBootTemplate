package com.tzesh.springtemplate.base.error;

import com.tzesh.springtemplate.base.exception.BaseException;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GenericErrorMessageTest {
    @Test
    @DisplayName("Should build GenericErrorMessage with all fields set correctly")
    void builder_createsInstanceWithFields() {
        // Arrange: set up test data
        String msg = "Error occurred";
        String details = "Some details";
        String path = "/api/test";
        // Act: build GenericErrorMessage
        GenericErrorMessage error = GenericErrorMessage.builder()
                .message(msg)
                .details(details)
                .path(path)
                .build();
        // Assert: verify all fields
        assertThat(error.getMessage()).isEqualTo(msg);
        assertThat(error.getDetails()).isEqualTo(details);
        assertThat(error.getPath()).isEqualTo(path);
    }

    @Test
    @DisplayName("Should construct GenericErrorMessage from BaseException and WebRequest")
    void constructor_withExceptionAndWebRequest_setsFields() {
        // Arrange: use a real NotFoundException instead of a mock
        GenericErrorMessage errorMsg = GenericErrorMessage.builder().message("Exception message").build();
        BaseException ex = new NotFoundException(errorMsg);
        WebRequest webRequest = Mockito.mock(WebRequest.class);
        Mockito.when(webRequest.getDescription(false)).thenReturn("desc");
        Mockito.when(webRequest.getContextPath()).thenReturn("/context");
        // Act: construct GenericErrorMessage
        GenericErrorMessage error = new GenericErrorMessage(ex, webRequest);
        // Assert: verify fields are set from exception and request
        assertThat(error.getMessage()).isEqualTo("Exception message");
        assertThat(error.getDetails()).isEqualTo("desc");
        assertThat(error.getPath()).isEqualTo("/context");
    }
}
