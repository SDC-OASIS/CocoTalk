package com.cocotalk.auth.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 HTTP 에러 코드
 */
@AllArgsConstructor
@Getter
public enum ResponseStatus {
    /*
     * 2XX Success
     */
    // 200 OK - 클라이언트의 요청을 서버가 정상적으로 처리했다.
    SUCCESS(200, 200, "요청에 성공하였습니다."),

    // 201 Created - 클라이언트의 요청을 서버가 정상적으로 처리했고 새로운 리소스가 생겼다.,
    CREATED(201, 200, "리소스 생성에 성공하였습니다."),

    // 204 No Content - 클라이언트의 요청은 정상적이다. 하지만 컨텐츠를 제공하지 않는다.
    NO_CONTENT(204, 200, "요청에 성공하였습니다."),

    /*
     * 4XX Client errors
     */
    // 400 Rad Request - 클라이언트의 요청이 유효하지 않아 더 이상 작업을 진행하지 않는 경우
    BAD_REQUEST(400, 2001, "요청에 실패하였습니다."),
    NO_VALUES(400, 2002, "입력되지 않은 값이 존재합니다."),
    EXISTS_INFO(400, 2003, "이미 존재하는 정보입니다."),

    // 401 Unauthorized - 클라이언트가 권한이 없기 때문에 작업을 진행할 수 없는 경우 (인증 정보 부족)
    UNAUTHORIZED(401, 2004, "권한이 없습니다."),

    // 403 Forbidden - 클라이언트가 권한이 없기 때문에 작업을 진행할 수 없는 경우 (요청을 이해했지만 권한이 없음)
    FORBIDDEN(403, 2005, "권한이 없습니다."),

    // 404 Not Found - 클라이언트가 요청한 자원이 존재하지 않다.
    NOT_FOUND(404, 2006, "NOT FOUND"),
    NOT_FOUND_FCM_TOKEN(404, 2007, "FCM TOKEN 기록이 존재하지 않습니다"),


    /*
     * 5XX Server errors
     */
    // 500 내부 서버 오류가 발생한 경우
    SERVER_ERROR(500, 2011, "서버와의 통신에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(500, 2012, "서버 내부에서 에러가 발생하였습니다."),
    DATABASE_ERROR(500, 2013, "데이터베이스 연결에 실패하였습니다."),
    PARSE_ERROR(500, 2014, "파싱 과정 중 에러가 발생했습니다.");

    private final int status;
    private final int code;
    private final String message;
}