package com.cocotalk.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomError {
    UNKNOWN(3000, "알 수 없는 에러입니다."),
    JWT_AUTHENTICATION(3001, "JWT 인중 중 문제가 발생했습니다."),
    JSON_PARSE(3002, "JSON 파싱 중 문제가 발생했습니다"),
    NOT_LOGIN(3003, "로그인 하지 않은 사용자입니다."),
    BAD_REQUEST(3004, "잘못된 요청입니다."),
    NOT_PERMITTED(3005, "권한이 없는 유저 입니다."),
    COMMUNICATION(3006, "통신 중 문제가 발생했습니다."),
    INPUT_OUTPUT(3007, "입출력 실행 중 문제가 발생했습니다."),
    STOMP(3008, "STOMP 메시징 중 문제가 발생했습니다.");

    private final int code;
    private final String desc;
    private final String type;
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
