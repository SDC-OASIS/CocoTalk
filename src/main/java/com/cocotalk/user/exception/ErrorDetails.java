package com.cocotalk.user.exception;

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

    public ErrorDetails(CustomException ce) {
        this.code = ce.getError().getCode();
        this.desc = ce.getMessage();
        this.type = ce.getError().getType();
    }

    public ErrorDetails(CustomError ce, Exception e) {
        this.code = ce.getCode();
        this.desc = e.getMessage();
        this.type = ce.toString();
    }

    public ErrorDetails(CustomError e) {
        this.code = e.getCode();
        this.desc = e.getDesc();
        this.type = e.toString();
    }

    public ErrorDetails(CustomError ce, String desc) {
        this.code = ce.getCode();
        this.desc = desc;
        this.type = ce.toString();
    }
}
