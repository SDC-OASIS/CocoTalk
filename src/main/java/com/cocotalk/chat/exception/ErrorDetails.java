package com.cocotalk.chat.exception;

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

    public ErrorDetails(CustomException e) {
        this.code = e.getError().getCode();
        this.desc = e.getMessage();
        this.type = e.getError().getType();
    }

    public ErrorDetails(CustomError e) {
        this.code = e.getCode();
        this.desc = e.getDesc();
        this.type = e.toString();
    }

    public ErrorDetails(CustomError e, String desc) {
        this.code = e.getCode();
        this.desc = desc;
        this.type = e.toString();
    }

//    public ErrorDetails(int errorCode, String desc) {
//        this.errorCode = errorCode;
//        this.desc = desc;
//        this.type = "UnknownException";
//    }
//
//    public ErrorDetails(int errorCode, String desc, String type) {
//        this.errorCode = errorCode;
//        this.desc = desc;
//        this.type = type;
//    }
}
