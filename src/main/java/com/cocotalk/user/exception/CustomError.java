package com.cocotalk.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomError {
    UNKNOWN(2001, "알 수 없는 에러입니다."),
    JWT_AUTHENTICATION(2002, "JWT 인중 중 문제가 발생했습니다."),
    JSON_PARSE(2003, "JSON 파싱 중 문제가 발생했습니다"),
    BAD_REQUEST(2004, "잘못된 요청입니다."),
    NOT_LOGIN(2005, "로그인 하지 않은 사용자입니다."),
    NOT_PERMITTED(2006, "권한이 없는 유저 입니다."),
    BAD_SQL(2007, "SQL 실행 중 문제가 발생했습니다.");

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
