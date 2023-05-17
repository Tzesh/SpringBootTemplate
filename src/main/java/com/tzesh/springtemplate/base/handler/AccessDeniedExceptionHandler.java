package com.tzesh.springtemplate.base.handler;


import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * EntityExceptionHandler is a class to handle exceptions
 * It is annotated with @ControllerAdvice so that it will be applied to all controllers
 * It is also annotated with @RestController so that it will return response body
 * It extends ResponseEntityExceptionHandler so that it can handle exceptions
 * @see ResponseEntityExceptionHandler
 * @see ControllerAdvice
 * @author tzesh
 */
@ControllerAdvice
@RestController
public class AccessDeniedExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * handleAccessDeniedException is a method to handle access denied exception
     * @param ex exception
     * @param request web request
     * @return ResponseEntity
     */
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<BaseResponse<GenericErrorMessage>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String message = ex.getMessage();
        String description = request.getDescription(false);

        var genericErrorMessage = new GenericErrorMessage(LocalDateTime.now(), message, description, request.getContextPath());

        return BaseResponse.error(genericErrorMessage, HttpStatus.FORBIDDEN).build();
    }
}
