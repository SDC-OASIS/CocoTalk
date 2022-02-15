package com.cocotalk.chat.exception;

import com.cocotalk.chat.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<ErrorDetails>> handleCustomException(CustomException e) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<ErrorDetails>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        e.printStackTrace();
        BindingResult bindingResult = e.getBindingResult();
        CustomError error = CustomError.BAD_REQUEST;
        String desc = error.getDesc() + " : " + bindingResult.getAllErrors().get(0).getDefaultMessage();
        ErrorDetails details = new ErrorDetails(error, desc);

        log.error("MethodArgumentNotValidException : " + desc);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }

    @MessageExceptionHandler
    public ErrorResponse<ErrorDetails> handleException(CustomException e) {
        e.printStackTrace();
        ErrorDetails details = new ErrorDetails(e);
        return new ErrorResponse<>(details);
    }
}
