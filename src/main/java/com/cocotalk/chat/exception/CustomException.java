package com.cocotalk.chat.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomError error;

    public CustomException(CustomError error) {
        super(error.getDesc());
        this.error = error;
    }

    public CustomException(CustomError error, String detailedDesc) {
        super(error.getDesc() + " : " + detailedDesc);
        this.error = error;
    }

    public CustomException(CustomError error, Throwable cause) {
        super(error.getDesc(), cause);
        this.error = error;
    }
}
