package com.cocotalk.user.controller;


import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.dto.response.UserResponse;
import com.cocotalk.user.service.UserService;
import com.cocotalk.user.support.GlobalResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 유저 관련 API 요청을 받는 컨트롤러
 *
 * @author ybell1028
 * @version 1.0, 최초 작성
 * @see com.cocotalk.user.service.UserService
 * @see com.cocotalk.user.mapper.UserMapper
 */

@RestController
@Api(tags = "유저 API")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 유저 전체 조회 API [GET] /api/user
     *
     * @return ResponseEntity<GlobalResponse<List<UserResponse>>> 조회된 유저 정보가 데이터에 포함됩니다.
     */
    @ApiOperation(value = "유저 전체 조회")
    @GetMapping
    public ResponseEntity<GlobalResponse<List<UserResponse>>> getAll() {
        List<UserResponse> data = userService.findAll();
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 id로 조회 API [GET] /api/user/{id}
     *
     * @param id 유저 id
     * @return ResponseEntity<GlobalResponse<UserResponse>> 조회된 유저 정보가 데이터에 포함됩니다.
     * @exception com.cocotalk.user.support.GlobalException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 id로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse<UserResponse>> findById(@PathVariable Long id) {
        UserResponse data = userService.findById(id);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 id로 정보 수정 API [PUT] /api/user/{id}
     *
     * @param id 유저 id
     * @param request 수정할 내용을 담은 요청 모델
     * @return ResponseEntity<GlobalResponse<UserResponse>> 수정된 유저 정보가 데이터에 포함됩니다.
     * @exception com.cocotalk.user.support.GlobalException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 id로 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<GlobalResponse<UserResponse>> modifyById(@PathVariable Long id, @RequestBody @Valid UserModifyRequest request) {
        UserResponse data = userService.modify(id, request);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 id로 삭제 API [DELETE] /api/user/{id}
     *
     * @param id 유저 id
     * @return ResponseEntity<GlobalResponse<String>> 삭제된 유저 cid를 포함한 메시지가 포함됩니다.
     * @exception com.cocotalk.user.support.GlobalException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 id로 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse<String>> deleteById(@PathVariable Long id) {
        String cid = userService.delete(id);
        String message = String.format("유저 %s이 삭제되었습니다.", cid);
        return new ResponseEntity<>(new GlobalResponse<>(message), HttpStatus.NO_CONTENT);
    }
}
