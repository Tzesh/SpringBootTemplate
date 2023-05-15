package com.tzesh.springtemplate.base.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * GenericErrorMessage is a generic error message class
 * @author tzesh
 */
@Data
@Builder
@AllArgsConstructor
public class GenericErrorMessage implements BaseErrorMessage {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private String path;
}
