package com.cocotalk.presence.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails { // CustomExceptionHanlder에서 사용함
    private int code;
    private String desc;
    private String type;

    public ErrorDetails(CustomException ce) {
        this.code = ce.getError().getCode();
        this.desc = ce.getMessage();
        this.type = ce.getError().getType(); // CustomException 담김
    }

    public ErrorDetails(CustomError ce, String desc) {
        this.code = ce.getCode();
        this.desc = desc;
        this.type = ce.toString(); // CustomError 이름 담김
    }

    public ErrorDetails(CustomError ce, Exception e) {
        this.code = ce.getCode();
        this.desc = e.getMessage(); // 각종 Exception 메시지
        this.type = e.toString(); // 각종 Exception 이름 담김
    }
}