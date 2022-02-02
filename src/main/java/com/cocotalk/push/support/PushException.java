package com.cocotalk.push.support;
import com.cocotalk.push.dto.common.response.ResponseStatus;
import lombok.Getter;

@Getter
public class PushException extends RuntimeException {
    private final ResponseStatus status;

    public PushException(ResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public PushException(ResponseStatus status, String errorMessage) {
        super(status.getMessage() + " : " + errorMessage);
        this.status = status;
    }

    public PushException(ResponseStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
    }
}
