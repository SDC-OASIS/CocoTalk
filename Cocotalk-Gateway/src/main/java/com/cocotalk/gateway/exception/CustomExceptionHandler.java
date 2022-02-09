package com.cocotalk.gateway.exception;

import com.cocotalk.gateway.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<?>> handle(CustomException e) {
        e.printStackTrace();
        CustomError error = e.getError();
        ErrorDetails details = new ErrorDetails(e);

        log.error("CustomException : " + e.getMessage());
        return ResponseEntity.status(error.getStatus()).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse<?>> handleUnknown(Exception e) {
        e.printStackTrace();
        String stackTrace = e.getStackTrace()[0].toString();
        ErrorDetails details = new ErrorDetails(CustomError.UNKNOWN, stackTrace);

        log.error("UnknownException : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse<>(details));
    }
}
