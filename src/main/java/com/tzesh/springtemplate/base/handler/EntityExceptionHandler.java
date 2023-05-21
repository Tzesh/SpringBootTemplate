package com.tzesh.springtemplate.base.handler;

import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.exception.BaseException;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import com.tzesh.springtemplate.base.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * EntityExceptionHandler is a class to handle exceptions
 * It is annotated with @ControllerAdvice so that it will be applied to all controllers
 * It is also annotated with @RestController so that it will return response body
 * It extends ResponseEntityExceptionHandler so that it can handle exceptions
 * @see ResponseEntityExceptionHandler
 * @see ControllerAdvice
 * @see RestController
 * @author tzesh
 */
@RestController
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class EntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * handleAllExceptions is a method to handle base exceptions
     * @param e exception
     * @param webRequest web request
     * @return ResponseEntity
     */
    @ExceptionHandler
    public final ResponseEntity<BaseResponse<GenericErrorMessage>> handleBaseException(BaseException e, WebRequest webRequest) {

        String message = e.getErrorMessage().getMessage();
        String description = webRequest.getDescription(false);

        var genericErrorMessage = new GenericErrorMessage(LocalDateTime.now(), message, description, webRequest.getContextPath());

        log.error("Base exception: {}", genericErrorMessage);

        return BaseResponse.error(genericErrorMessage, HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * handleNotFoundException is a method to handle not found exception
     * @param e exception
     * @param webRequest web request
     * @return ResponseEntity
     */
    @ExceptionHandler
    public final ResponseEntity<BaseResponse<GenericErrorMessage>> handleNotFoundException(NotFoundException e, WebRequest webRequest) {

        String message = e.getErrorMessage().getMessage();
        String description = webRequest.getDescription(false);

        var genericErrorMessage = new GenericErrorMessage(LocalDateTime.now(), message, description, webRequest.getContextPath());

        log.error("Not found exception: {}", genericErrorMessage);

        return BaseResponse.error(genericErrorMessage, HttpStatus.NOT_FOUND).build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        var genericErrorMessage = new GenericErrorMessage(LocalDateTime.now(), "Validation Failed", errors.toString(), request.getContextPath());

        var response = BaseResponse.error(genericErrorMessage, HttpStatus.BAD_REQUEST);

        log.error("Validation failed: {}", genericErrorMessage);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
