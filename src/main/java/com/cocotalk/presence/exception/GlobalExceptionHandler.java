package com.cocotalk.presence.exception;

import com.cocotalk.presence.dto.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<?>> handleCustomException(CustomException e) {
        CustomError error = e.getError();
        ErrorDetails details;
        if(e.getCause() != null) {
            Throwable cause = e.getCause();
            details = new ErrorDetails(error, cause);
            log.error("CustomException : " + cause.getMessage());
        } else {
            details = new ErrorDetails(e);
            log.error("CustomException : " + e.getMessage());
        }

        return ResponseEntity.status(error.getStatus()).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse<?>> handleJsonProcessingException(JsonProcessingException e) {
        ErrorDetails details = new ErrorDetails(CustomError.BAD_REQUEST, e);

        log.error("JsonProcessingException : " + details.getDesc());
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }
}