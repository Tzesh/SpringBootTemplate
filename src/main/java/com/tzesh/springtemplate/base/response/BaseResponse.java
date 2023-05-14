package com.tzesh.springtemplate.base.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author tzesh
 * @param <T> type of the response
 * BaseResponse is a generic class that is used to store data about responses.
 * @see Serializable
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private T data;
    private String message;
    private boolean success;
    private LocalDateTime timestamp;


    /**
     * Constructor for response with data
     * @param data data to be stored
     * @param success success status
     */
    public BaseResponse(T data, boolean success) {
        this.data = data;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Response with data and success status
     * @param data data to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(data, true);
    }

    /**
     * Empty response with success status
     * @return BaseResponse
     * @param <T> data type
     */
    public static <T> BaseResponse<T> empty() {
        return new BaseResponse<>(null, true);
    }

    /**
     * Response with data and error status
     * @param data message to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> error(T data) {
        return new BaseResponse<>(data, false);
    }

    public BaseResponse<T> message(String message) {
        this.message = message;
        return this;
    }
}
