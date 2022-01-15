package com.cocotalk.chat.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class GlobalResponse<T> {

    // private boolean success;
    private T data;
    private HttpStatus status;
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;

    public GlobalResponse(T data) {
        this.data = data;
        this.status = HttpStatus.OK;
        this.statusCode = 200;
        this.timestamp = LocalDateTime.now();
    }

    public GlobalResponse(T data, HttpStatus status) {
        this.data = data;
        this.status = status;
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
    }

    public GlobalResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
    }
}
