package com.tzesh.springtemplate.controller.base.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @param <T> type of the response
 *            BaseResponse is a generic class that is used to store data about responses.
 * @author tzesh
 * @see Serializable
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private T data;
    private String message;
    private boolean success;
    private LocalDateTime timestamp;
    private String status;
}
