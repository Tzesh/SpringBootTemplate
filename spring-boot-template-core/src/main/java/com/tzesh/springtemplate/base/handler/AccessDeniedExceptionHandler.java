package com.tzesh.springtemplate.base.handler;


import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AccessDeniedExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * handleAccessDeniedException is a method to handle access denied exception
     * @param ex exception
     * @param request web request
     * @return ResponseEntity
     */
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<BaseResponse<GenericErrorMessage>> handleAccessDeniedException(final AccessDeniedException ex, final WebRequest request) {
        final String message = ex.getMessage();
        final String description = request.getDescription(false);
        final GenericErrorMessage genericErrorMessage = new GenericErrorMessage(message, description, request.getContextPath());
        log.error("Access denied exception: {}", genericErrorMessage);

        return BaseResponse.error(genericErrorMessage, HttpStatus.FORBIDDEN).build();
    }
}
