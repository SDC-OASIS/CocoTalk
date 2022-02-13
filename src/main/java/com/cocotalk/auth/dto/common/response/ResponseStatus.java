package com.cocotalk.auth.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
*
* Response Code
* 성공일 경우 code에 200, 실패일 경우 error용 코드를 보내줌
*
 */
@AllArgsConstructor
@Getter
public enum ResponseStatus {
    /*
     *  Success
     */
    SUCCESS("요청에 성공하였습니다."),
    CREATED("리소스 생성에 성공하였습니다."),
    NO_CONTENT(  "요청에 성공하였습니다."),

    /*
     *  Fail
     */
    BAD_REQUEST( 2001, "요청에 실패하였습니다."),
    NO_VALUES(2002, "입력되지 않은 값이 존재합니다."),
    EXISTS_INFO( 2003, "이미 존재하는 정보입니다."),

    UNAUTHORIZED(2004, "권한이 없습니다."),

    FORBIDDEN(2005, "권한이 없습니다."),

    NOT_FOUND( 2006, "NOT FOUND"),
    NOT_FOUND_FCM_TOKEN(2007, "FCM TOKEN 기록이 존재하지 않습니다"),

    SERVER_ERROR(2011, "서버와의 통신에 실패하였습니다."),
    INTERNAL_SERVER_ERROR( 2012, "서버 내부에서 에러가 발생하였습니다."),
    DATABASE_ERROR( 2013, "데이터베이스 연결에 실패하였습니다."),
    PARSE_ERROR( 2014, "파싱 과정 중 에러가 발생했습니다.");

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