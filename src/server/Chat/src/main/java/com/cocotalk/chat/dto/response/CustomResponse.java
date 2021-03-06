package com.cocotalk.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class CustomResponse<T> {

    // private boolean success;
    private T data;
    private HttpStatus status;
    private int statusCode;
    private String message;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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
