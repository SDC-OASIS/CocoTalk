package com.cocotalk.chat.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalError {
    UNKNOWN_ERROR(2001, "알 수 없는 에러입니다."),
    BAD_REQUEST(2002, "잘못된 요청입니다."),
    NOT_LOGIN(2003, "로그인 하지 않은 사용자입니다."),
    NOT_PERMITTED(2004, "권한이 없는 유저 입니다.");

    private final int errorCode;
    private final String desc;
    private final HttpStatus status;

    GlobalError(int errorCode, String desc) {
        this.errorCode = errorCode;
        this.desc = desc;
        this.status = HttpStatus.OK;
    }

    GlobalError(int errorCode, String desc, HttpStatus status) {
        this.errorCode = errorCode;
        this.desc = desc;
        this.status = status;
    }
}
