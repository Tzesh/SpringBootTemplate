package com.tzesh.springtemplate.base.exception;

import com.tzesh.springtemplate.base.error.BaseErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * SaveFailedException is an exception class for save failed error
 * It is annotated with @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
 * so that it will return 500 status code when it is thrown
 * @see BaseException
 * @author tzesh
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SaveFailedException extends BaseException {
    public SaveFailedException(BaseErrorMessage errorMessage) {
        super(errorMessage);
    }
}
