package com.cocotalk.presence.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomError {
    UNKNOWN(5000, "알 수 없는 에러입니다."),
    JWT_AUTHENTICATION(5001, "JWT 인중 중 문제가 발생했습니다."),
    JSON_PARSE(5002, "JSON 파싱 중 문제가 발생했습니다"),
    NOT_LOGIN(5003, "로그인 하지 않은 사용자입니다."),
    BAD_REQUEST(5004, "잘못된 요청입니다."),
    NOT_PERMITTED(5005, "권한이 없는 유저 입니다."),
    REDIS(5006, "Redis 통신 중 문제가 발생했습니다"),
    CHAT_SERVER_CONNECTION(5007, "채팅 서버 연결 중 문제가 발생했습니다");

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
}
