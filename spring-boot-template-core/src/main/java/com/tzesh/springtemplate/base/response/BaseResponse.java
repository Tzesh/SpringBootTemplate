package com.tzesh.springtemplate.base.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author tzesh
 * @param <T> type of the response
 * BaseResponse is a generic class that is used to store data about responses.
 * @see Serializable
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private T data;
    private String message;
    private boolean success;
    private LocalDateTime timestamp;
    private HttpStatus status;


    /**
     * Constructor for response with data
     * @param data data to be stored
     * @param success success status
     */
    public BaseResponse(T data, HttpStatus status, boolean success) {
        this.timestamp = LocalDateTime.now();
        this.data = data;
        this.status = status;
        this.success = success;
    }

    /**
     * Response with data and success status
     * @param data data to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(data, HttpStatus.OK, true);
    }

    /**
     * Response with data and success status
     * @param data data to be stored
     * @param status status to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> create(T data, HttpStatus status) {
        return new BaseResponse<>(data, status, true);
    }

    /**
     * Response with data and success status
     * @param data data to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> created(T data) {
        return new BaseResponse<>(data, HttpStatus.CREATED, true);
    }

    /**
     * Response with data and success status
     * @param data data to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> notFound(T data) {
        return new BaseResponse<>(data, HttpStatus.NOT_FOUND, false);
    }

    /**
     * Response with data and success status
     * @param data data to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> internalServerError(T data) {
        return new BaseResponse<>(data, HttpStatus.INTERNAL_SERVER_ERROR, false);
    }

    /**
     * Empty response with success status
     * @return BaseResponse
     * @param <T> data type
     */
    public static <T> BaseResponse<T> empty() {
        return new BaseResponse<>(null, HttpStatus.NO_CONTENT, true);
    }

    /**
     * Response with data and error status
     * @param data message to be stored
     * @return BaseResponse<T>
     * @param <T> data type
     */
    public static <T> BaseResponse<T> error(T data, HttpStatus status) {
        return new BaseResponse<>(data, status, false).message("An error has occurred");
    }

    /**
     * Add message to response
     * @param message message to be stored
     * @return BaseResponse<T>
     */
    public BaseResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Build response to ResponseEntity
     * @return ResponseEntity
     */
    public ResponseEntity<BaseResponse<T>> build() {
        return new ResponseEntity<>(this, this.status);
    }
}
