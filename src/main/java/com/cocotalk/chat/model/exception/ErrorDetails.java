package com.cocotalk.chat.model.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private int errorCode;
    private String desc;
    private String type;
    private String stackTrace; // debug용

    public ErrorDetails(GlobalException e) {
        this.errorCode = e.getError().getErrorCode();
        this.desc = e.getMessage();
        this.type = e.toString();
        this.stackTrace = e.getStackTrace()[0].toString();
    }

    public ErrorDetails(GlobalError e, String stackTrace) {
        this.errorCode = e.getErrorCode();
        this.desc = e.getDesc();
        this.type = e.toString();
        this.stackTrace = stackTrace;
    }

    public ErrorDetails(GlobalError e, String desc, String stackTrace) {
        this.errorCode = e.getErrorCode();
        this.desc = desc;
        this.type = e.toString();
        this.stackTrace = stackTrace;
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
