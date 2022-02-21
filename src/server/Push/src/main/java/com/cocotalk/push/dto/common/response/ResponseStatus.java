package com.cocotalk.push.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * Response에 사용되는 ResponseStatus
 * 실패일 경우 error용 코드를 보내줍니다.
 *
 */
@AllArgsConstructor
@Getter
public enum ResponseStatus {
    /*
     *  Success
     */
    SUCCESS( "요청에 성공하였습니다."),
    CREATED(  "리소스 생성에 성공하였습니다."),
    ACCEPTED(  "요청에 성공하였습니다."),
    NO_CONTENT( "요청에 성공하였습니다."),

    /*
     *  Fail
     */
    BAD_REQUEST( 4001, "요청에 실패하였습니다."),
    NO_VALUES( 4002, "입력되지 않은 값이 존재합니다."),
    EXISTS_INFO(4003, "이미 존재하는 정보입니다."),

    UNAUTHORIZED( 4004, "권한이 없습니다."),
    FORBIDDEN( 4005, "권한이 없습니다."),

    NOT_FOUND(4006, "NOT FOUND"),

    METHOD_NOT_ALLOWED( 4007, "허용되지 않는 HTTP Method 입니다."),

    CONFLICT(4008, "충돌이 발생하였습니다."),

    TOO_MANY_REQUESTS(4009, "요청이 너무 많습니다."),

    SERVER_ERROR(4010, "서버와의 통신에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(4011, "서버 내부에서 에러가 발생하였습니다."),
    DATABASE_ERROR(4012, "데이터베이스 연결에 실패하였습니다."),
    SUBSCRIBE_ERROR( 4013, "구독 중 에러가 발생했습니다."),
    PARSE_ERROR(4014, "파싱 과정 중 에러가 발생했습니다.");

    private final Integer code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    ResponseStatus(String message) {
        this.code = null;
        this.message = message;
    }
}