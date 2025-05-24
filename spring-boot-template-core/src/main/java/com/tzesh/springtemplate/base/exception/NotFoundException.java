package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * NotFoundException is an exception class for not found error
 * It is annotated with @ResponseStatus(HttpStatus.NOT_FOUND)
 * so that it will return 404 status code when it is thrown
 * @author tzesh
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseException {
    public NotFoundException(final BaseErrorMessage errorMessage) {
        super(errorMessage);
    }
}
