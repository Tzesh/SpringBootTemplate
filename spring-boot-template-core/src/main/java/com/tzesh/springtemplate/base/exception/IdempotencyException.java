package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown for idempotency violations.
 * Returns HTTP 409 (Conflict).
 * @author tzesh
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class IdempotencyException extends BaseException {
    public IdempotencyException(final BaseErrorMessage errorMessage) {
        super(errorMessage);
    }
}
