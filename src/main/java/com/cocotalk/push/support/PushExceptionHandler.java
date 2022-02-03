package com.cocotalk.push.support;

import com.cocotalk.push.dto.common.response.Response;
import com.cocotalk.push.dto.common.response.ResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import static com.cocotalk.push.dto.common.response.ResponseStatus.NO_VALUES;


@Slf4j
@RestControllerAdvice
public class PushExceptionHandler {
    @ExceptionHandler(PushException.class)
    public ResponseEntity<Response<?>> serverException(PushException e) {
        ResponseStatus status = e.getStatus();
        log.error("PushException : " + status.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(status));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
        String objName = bindingResult.getAllErrors().get(0).getObjectName();
        String code = bindingResult.getAllErrors().get(0).getCodes()[0];
        log.error("MethodArgumentNotValidException : " + errorMessage +" ("+ code +") at " + objName);
        return ResponseEntity.status(HttpStatus.OK).body(new Response<>(NO_VALUES));
    }

}