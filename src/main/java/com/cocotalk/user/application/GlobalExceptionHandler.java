package com.cocotalk.user.application;

import com.cocotalk.user.dto.exception.ErrorDetails;
import com.cocotalk.user.dto.exception.GlobalError;
import com.cocotalk.user.dto.exception.GlobalException;
import com.cocotalk.user.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse<?>> ChatServerException(GlobalException e) {
        GlobalError error = e.getError();
        ErrorDetails details = new ErrorDetails(e);

        log.error("GlobalException : " + e.getMessage());
        return ResponseEntity.status(error.getStatus()).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        GlobalError error = GlobalError.BAD_REQUEST;
        String desc = error.getDesc() + " : " + bindingResult.getAllErrors().get(0).getDefaultMessage();
        String stackTrace = e.getStackTrace()[0].toString();
        ErrorDetails details = new ErrorDetails(error, desc, stackTrace);

        log.error("MethodArgumentNotValidException : " + desc);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse<?>> unknownException(Exception e) {
        String stackTrace = e.getStackTrace()[0].toString();
        ErrorDetails details = new ErrorDetails(GlobalError.UNKNOWN_ERROR, stackTrace);

        log.error("UnknownException : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse<>(details));
    }
}
