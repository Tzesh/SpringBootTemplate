package com.tzesh.springtemplate.base.error;

import com.tzesh.springtemplate.base.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * GenericErrorMessage is a generic error message class
 * @author tzesh
 */
@Data
@Builder
@AllArgsConstructor
public class GenericErrorMessage implements BaseErrorMessage {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private String details;
    private String path;

    /**
     * Constructor from BaseException and WebRequest
     * @param e the BaseException instance
     * @param webRequest the WebRequest instance
     */
    public GenericErrorMessage(final BaseException e, final WebRequest webRequest) {
        final String message = e.getErrorMessage().getMessage();
        final String description = webRequest.getDescription(false);
        this.message = message;
        this.details = description;
        this.path = webRequest.getContextPath();
    }
}
