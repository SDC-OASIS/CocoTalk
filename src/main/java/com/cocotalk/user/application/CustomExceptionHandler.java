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

import java.sql.SQLException;

import static com.cocotalk.user.exception.CustomError.BAD_REQUEST;
import static com.cocotalk.user.exception.CustomError.BAD_SQL;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<?>> handle(CustomException e) {
        CustomError error = e.getError();
        ErrorDetails details = new ErrorDetails(e);

        log.error("CustomException : " + e.getMessage());
        return ResponseEntity.status(error.getStatus()).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        CustomError error = BAD_REQUEST;
        String desc = error.getDesc() + " : " + bindingResult.getAllErrors().get(0).getDefaultMessage();
        ErrorDetails details = new ErrorDetails(error, desc);

        log.error("MethodArgumentNotValidException : " + desc);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse<?>> handleSqlException(SQLException e) {
        e.printStackTrace();
        CustomError error = BAD_SQL;
        ErrorDetails details = new ErrorDetails(error, e);
        log.error("SQLException : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse<?>> handleFileUploadException(FileUploadException e) {
        e.printStackTrace();
        ErrorDetails details = new ErrorDetails(CustomError.BAD_REQUEST, e);
        log.error("FileUploadException : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse<>(details));
    }
}
