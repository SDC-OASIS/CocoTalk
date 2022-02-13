package com.cocotalk.auth.exception;

import com.cocotalk.auth.dto.common.response.ResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResponseCode status;

    public CustomException(ResponseCode status) {
        super(status.getMessage());
        this.status = status;
    }

    public CustomException(ResponseCode status, String errorMessage) {
        super(status.getMessage() + " : " + errorMessage);
        this.status = status;
    }

    public CustomException(ResponseCode status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
    }
}
