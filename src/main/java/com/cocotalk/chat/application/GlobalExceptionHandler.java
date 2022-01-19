package com.cocotalk.chat.application;

import com.cocotalk.chat.model.exception.GlobalError;
import com.cocotalk.chat.model.exception.GlobalException;
import com.cocotalk.chat.model.response.GlobalResponse;
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
    public ResponseEntity<GlobalResponse<?>> UserServerException(GlobalException e) {
        GlobalError error = e.getError();
        log.error("GlobalException : " + error.getDesc());

        return ResponseEntity
                .status(error.getStatus())
                .body(new GlobalResponse<>(error.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
        log.error("MethodArgumentNotValidException : " + errorMessage);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GlobalResponse<>(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalResponse<?>> unknownException(Exception e) {
        log.error("UnknownException : " + e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GlobalResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, GlobalError.UNKNOWN_ERROR.getDesc()));
    }
}
