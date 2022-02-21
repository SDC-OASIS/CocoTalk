package com.cocotalk.auth.dto.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


/**
 *
 * 요청이 성공인 경우 code는 포함되지 않습니다.
 * 요청이 실패한 경우 result는 포함되지 않습니다.
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@JsonPropertyOrder({"isSuccess", "status", "code", "message", "result", "timestamp"})
public class Response<T> {
    private Boolean isSuccess;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;
    private Date timestamp;

    /*
     성공 시 호출
     */
    public Response(T result, ResponseStatus status) {
        this.isSuccess = true;
        this.message = status.getMessage();
        this.result = result;
        this.timestamp = new Date();
    }

    /*
     실패 시 호출
     */
    public Response(ResponseStatus status) {
        this.isSuccess = false;
        this.code = status.getCode();
        this.message = status.getMessage();
        this.timestamp = new Date();
    }

}
