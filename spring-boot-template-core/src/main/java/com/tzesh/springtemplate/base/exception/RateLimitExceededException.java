package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a rate limit is exceeded.
 * Returns HTTP 429 (Too Many Requests).
 * @author tzesh
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends BaseException {
    public RateLimitExceededException(final BaseErrorMessage errorMessage) {
        super(errorMessage);
    }
}
