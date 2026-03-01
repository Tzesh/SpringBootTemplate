package com.tzesh.springtemplate.base.handler;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.response.BaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AccessDeniedExceptionHandlerTest {
    @Test
    @DisplayName("Should return 403 Forbidden and correct error details when access is denied")
    void handleAccessDeniedException_returnsForbiddenResponse() {
        // Arrange: create handler, exception, and mock request
        AccessDeniedExceptionHandler handler = new AccessDeniedExceptionHandler();
        AccessDeniedException ex = new AccessDeniedException("Denied");
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("desc");
        Mockito.when(request.getContextPath()).thenReturn("/context");

        // Act: call handler
        ResponseEntity<BaseResponse<GenericErrorMessage>> response = handler.handleAccessDeniedException(ex, request);

        // Assert: verify status and error details
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getMessage()).isNotNull();
        assertThat(response.getBody().getData().getMessage()).isEqualTo("Denied");
        assertThat(response.getBody().getData().getDetails()).isEqualTo("desc");
        assertThat(response.getBody().getData().getPath()).isEqualTo("/context");
    }
}