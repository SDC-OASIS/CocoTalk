package com.cocotalk.user.controller;

import com.cocotalk.user.dto.request.FriendAddRequest;
import com.cocotalk.user.dto.response.FriendResponse;
import com.cocotalk.user.service.FriendService;
import com.cocotalk.user.support.GlobalResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * 친구 관련 API 요청을 받는 컨트롤러
 *
 * @author ybell1028
 * @version 1.0, 최초 작성
 * @see com.cocotalk.user.service.FriendService
 */

@Slf4j
@Api(tags = "친구 API")
@RestController
@RequestMapping("/api/user/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    /**
     * 친구 추가 API [POST] /api/user/friend
     *
     * @param friendAddRequest 친구 추가를 요청하는 유저 id와 친구로 추가되는 유저 id를 포함한 요청 모델
     * @return ResponseEntity<GlobalResponse<FriendResponse>> 추가된 친구 정보가 데이터에 포함됩니다.
     * @exception
     */
    @ApiOperation(value = "친구 추가")
    @PostMapping("/")
    public ResponseEntity<GlobalResponse<FriendResponse>> add(@RequestBody @Valid FriendAddRequest friendAddRequest) {
        FriendResponse data = friendService.add(friendAddRequest);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }
}