package com.cocotalk.user.application;

import com.cocotalk.user.dto.response.ErrorResponse;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.exception.ErrorDetails;
import com.cocotalk.user.exception.CustomError;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.cocotalk.user.exception.CustomError.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<?>> ChatServerException(CustomException e) {
        CustomError error = e.getError();
        ErrorDetails details = new ErrorDetails(e);

        log.error("CustomException : " + e.getMessage());
        return ResponseEntity.status(error.getStatus()).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        CustomError error = BAD_REQUEST;
        String desc = error.getDesc() + " : " + bindingResult.getAllErrors().get(0).getDefaultMessage();
        String stackTrace = e.getStackTrace()[0].toString();
        ErrorDetails details = new ErrorDetails(error, desc, stackTrace);

        log.error("MethodArgumentNotValidException : " + desc);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse<?>> unknownException(Exception e) {
        String stackTrace = e.getStackTrace()[0].toString();
        ErrorDetails details = new ErrorDetails(CustomError.UNKNOWN, stackTrace);

        log.error("UnknownException : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse<?>> fileUploadException(FileUploadException e) {
        String stackTrace = e.getStackTrace()[0].toString();
        ErrorDetails details = new ErrorDetails(CustomError.BAD_REQUEST, stackTrace);
        log.error("FileUploadException : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }
}
