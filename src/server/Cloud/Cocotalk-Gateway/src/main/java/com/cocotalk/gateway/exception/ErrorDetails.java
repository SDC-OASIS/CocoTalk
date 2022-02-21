package com.cocotalk.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private int code;
    private String desc;
    private String type;

    public ErrorDetails(CustomException exception) {
        this.code = exception.getError().getCode();
        this.desc = exception.getMessage();
        this.type = exception.getError().getType();
    }

    public ErrorDetails(CustomError error, String desc) {
        this.code = error.getCode();
        this.desc = desc;
        this.type = error.getType();
    }

    public ErrorDetails(CustomError error, Throwable cause) {
        this.code = error.getCode();
        this.desc = cause.getMessage();
        this.type = error.getType();
    }
}
