package com.tzesh.springtemplate.base.handler;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.exception.BaseException;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import com.tzesh.springtemplate.base.response.BaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class EntityExceptionHandlerTest {
    @Test
    @DisplayName("Should return 500 Internal Server Error and correct error details for BaseException")
    void handleBaseException_returnsErrorResponse() {
        // Arrange: use a real NotFoundException instead of a mock
        EntityExceptionHandler handler = new EntityExceptionHandler();
        GenericErrorMessage errorMsg = GenericErrorMessage.builder().message("Base error").details("details").path("/base").build();
        BaseException ex = new NotFoundException(errorMsg);
        WebRequest request = Mockito.mock(WebRequest.class);
        Mockito.when(request.getDescription(false)).thenReturn("details");
        Mockito.when(request.getContextPath()).thenReturn("/base");

        // Act: call handler
        ResponseEntity<BaseResponse<GenericErrorMessage>> response = handler.handleBaseException(ex, request);

        // Assert: verify status and error details
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getMessage()).isEqualTo("Base error");
        assertThat(response.getBody().getData().getDetails()).isEqualTo("details");
        assertThat(response.getBody().getData().getPath()).isEqualTo("/base");
    }
}
