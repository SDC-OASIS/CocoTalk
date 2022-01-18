package com.cocotalk.support;

import com.cocotalk.response.ResponseStatus;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final ResponseStatus status;

    public AuthException(ResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public AuthException(ResponseStatus status, String errorMessage) {
        super(status.getMessage() + " : " + errorMessage);
        this.status = status;
    }

    public AuthException(ResponseStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
    }
}
