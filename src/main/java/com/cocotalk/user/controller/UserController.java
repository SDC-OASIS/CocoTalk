package com.cocotalk.user.controller;


import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.dto.response.GlobalResponse;
import com.cocotalk.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * @see com.cocotalk.user.utils.mapper.UserMapper
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
    public ResponseEntity<GlobalResponse<List<UserVo>>> findAll() {
        List<UserVo> data = userService.findAll();
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 id로 조회 API [GET] /api/user/{id}
     *
     * @param id 유저 id
     * @return ResponseEntity<GlobalResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     * @exception com.cocotalk.user.dto.exception.GlobalException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 id로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse<UserVo>> findById(@PathVariable Long id) {
        UserVo data = userService.findById(id);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 코코톡 id로 조회 API [GET] /api/user?cid=
     *
     * @param cid 코코톡 id
     * @return ResponseEntity<GlobalResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     * @exception com.cocotalk.user.dto.exception.GlobalException 해당 코코톡 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 cid로 조회")
    @GetMapping("/cid/{cid}")
    public ResponseEntity<GlobalResponse<UserVo>> findByCid(
            @ApiParam(value = "유저 코코톡 id", required = true, example = "ybell1028")
            @RequestParam String cid) {
        UserVo data = userService.findByCid(cid);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 email로 조회 API [GET] /api/user?email=
     *
     * @param email 유저 email
     * @return ResponseEntity<GlobalResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     * @exception com.cocotalk.user.dto.exception.GlobalException 해당 email을 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 email로 조회")
    @GetMapping("/email/{email}")
    public ResponseEntity<GlobalResponse<UserVo>> findByEmail(
            @ApiParam(value = "유저 email", required = true, example = "test@example.com")
            @RequestParam String email) {
        UserVo data = userService.findByEmail(email);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 연락처로 조회 API [GET] /api/user?phone=[ string, ... ]
     *
     * @param phones 유저들의 연락처
     * @return ResponseEntity<GlobalResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     */
    @ApiOperation(value = "유저 연락처로 조회")
    @GetMapping("/phone")
    public ResponseEntity<GlobalResponse<List<UserVo>>> findByPhone(
            @ApiParam(value = "연락처", example = "[ \"01012345678\", \"01011112222\", ... ]")
            @RequestParam List<String> phones) {
        List<UserVo> data = userService.findByPhones(phones);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }


    /**
     * 유저 정보 수정 API [PUT] /api/user/{id}
     *
     * @param request 수정할 내용을 담은 요청 모델
     * @return ResponseEntity<GlobalResponse<UserVo>> 수정된 유저 정보가 데이터에 포함됩니다.
     * @exception com.cocotalk.user.dto.exception.GlobalException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 수정")
    @PutMapping()
    public ResponseEntity<GlobalResponse<UserVo>> modifyById(User user, @RequestBody @Valid UserModifyRequest request) {
        UserVo data = userService.modify(user, request);
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 삭제 API [DELETE] /api/user
     *
     * @return ResponseEntity<GlobalResponse<String>> 삭제된 유저 cid를 포함한 메시지가 포함됩니다.
     * @exception com.cocotalk.user.dto.exception.GlobalException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @ApiOperation(value = "유저 삭제")
    @DeleteMapping
    public ResponseEntity<GlobalResponse<String>> deleteById(User user) {
        String result = userService.delete(user);
        return new ResponseEntity<>(new GlobalResponse<>(result), HttpStatus.OK);
    }
}
