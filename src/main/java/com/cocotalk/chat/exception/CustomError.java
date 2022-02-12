package com.cocotalk.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomError {
    UNKNOWN(4001, "알 수 없는 에러입니다."),
    JWT_AUTHENTICATION(4002, "JWT 인중 중 문제가 발생했습니다."),
    JSON_PARSE(4003, "JSON 파싱 중 문제가 발생했습니다"),
    BAD_REQUEST(4004, "잘못된 요청입니다."),
    NOT_LOGIN(4005, "로그인 하지 않은 사용자입니다."),
    NOT_PERMITTED(4006, "권한이 없는 유저 입니다."),
    COMMUNICATION(4007, "통신 중 문제가 발생했습니다.");

    private final int code;
    private final String desc;
    private final String type; // Exception 종류
    private final HttpStatus status;

    CustomError(int code, String desc) {
        this.code = code;
        this.desc = desc;
        this.type = this.toString();
        this.status = HttpStatus.OK;
    }

    CustomError(int code, String desc, String type) {
        this.code = code;
        this.desc = desc;
        this.type = type;
        this.status = HttpStatus.OK;
    }

    CustomError(int code, String desc, String type, HttpStatus status) {
        this.code = code;
        this.desc = desc;
        this.type = type;
        this.status = status;
    }
}
