package com.cocotalk.push.exception;
import com.cocotalk.push.dto.common.response.ResponseStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResponseStatus status;

    public CustomException(ResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public CustomException(ResponseStatus status, String errorMessage) {
        super(status.getMessage() + " : " + errorMessage);
        this.status = status;
    }

    public CustomException(ResponseStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
    }
}
