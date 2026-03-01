package com.tzesh.springtemplate.base.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BaseResponseTest {
    @Test
    @DisplayName("Should build BaseResponse with all fields set correctly using builder")
    void builder_createsInstanceWithFields() {
        // Arrange: set up test data
        String msg = "Success";
        boolean success = true;
        HttpStatus status = HttpStatus.OK;
        LocalDateTime now = LocalDateTime.now();
        // Act: build BaseResponse
        BaseResponse<String> response = BaseResponse.<String>builder()
                .data("data")
                .message(msg)
                .success(success)
                .status(status)
                .timestamp(now)
                .build();
        // Assert: verify all fields
        assertThat(response.getData()).isEqualTo("data");
        assertThat(response.getMessage()).isEqualTo(msg);
        assertThat(response.isSuccess()).isEqualTo(success);
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getTimestamp()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should construct BaseResponse with data, status, and success")
    void constructor_withDataStatusSuccess_setsFields() {
        // Act: construct BaseResponse
        BaseResponse<String> response = new BaseResponse<>("data", HttpStatus.CREATED, true);
        // Assert: verify fields
        assertThat(response.getData()).isEqualTo("data");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getTimestamp()).isNotNull();
    }
}

