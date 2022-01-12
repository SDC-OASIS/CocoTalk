package com.cocotalk.user.support;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalError {
    USER_NOT_EXIST("10001", "유저가 존재하지 않습니다.", HttpStatus.OK);

    private final String errorCode;
    private final String desc;
    private final HttpStatus status;

    GlobalError(String errorCode, String desc, HttpStatus status) {
        this.errorCode = errorCode;
        this.desc = desc;
        this.status = status;
    }
}
