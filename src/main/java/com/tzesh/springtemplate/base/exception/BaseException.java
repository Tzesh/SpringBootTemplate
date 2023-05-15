package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.BaseErrorMessage;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BaseException is a base exception class for all exceptions in this project
 * It is annotated with @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 * so that it will return 500 status code when it is thrown
 * @author tzesh
 */
@Data
@RequiredArgsConstructor
@MappedSuperclass
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BaseException extends RuntimeException {
    protected final BaseErrorMessage errorMessage;
}
