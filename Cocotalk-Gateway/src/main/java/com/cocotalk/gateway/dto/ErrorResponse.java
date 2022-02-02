package com.cocotalk.gateway.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class ErrorResponse<T> {
    private T error;
    private HttpStatus status;
    private int statusCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(T error) {
        this.error = error;
        this.status = HttpStatus.OK;
        this.statusCode = 200;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(T error, HttpStatus status) {
        this.error = error;
        this.status = status;
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
    }
}

