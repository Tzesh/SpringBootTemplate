package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * UnauthorizedException is an exception class for unauthorized error
 * It is annotated with @ResponseStatus(HttpStatus.UNAUTHORIZED)
 * so that it will return 401 status code when it is thrown
 * @author tzesh
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {
    public UnauthorizedException(BaseErrorMessage message) {
        super(message);
    }
}
