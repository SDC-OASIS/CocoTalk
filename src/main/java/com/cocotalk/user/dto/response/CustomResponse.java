package com.cocotalk.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class CustomResponse<T> {
    private T data;
    private HttpStatus status;
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;

    public CustomResponse(T data) {
        this.data = data;
        this.status = HttpStatus.OK;
        this.statusCode = 200;
        this.timestamp = LocalDateTime.now();
    }

    public CustomResponse(T data, HttpStatus status) {
        this.data = data;
        this.status = status;
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
    }

    public CustomResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
    }
}
