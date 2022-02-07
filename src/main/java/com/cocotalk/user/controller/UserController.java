package com.cocotalk.user.controller;


import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.UserVo;
import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.dto.response.CustomResponse;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "유저 API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 유저 전체 조회 API [GET] /user
     *
     * @return ResponseEntity<CustomResponse<List<UserResponse>>> 조회된 유저 정보가 데이터에 포함됩니다.
     */
    @Operation(summary = "유저 전체 조회")
    @GetMapping
    public ResponseEntity<CustomResponse<List<UserVo>>> findAll() {
        List<UserVo> data = userService.findAll();
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 accessToken으로 조회 API [GET] /user/{id}
     *
     * @return ResponseEntity<CustomResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     * @exception CustomException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @Operation(summary = "유저 Access Token으로 조회")
    @GetMapping("/token")
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<UserVo>> findByAccessToken(@Parameter(hidden = true) User user) {
        UserVo data = userService.findByAccessToken(user);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 id로 조회 API [GET] /user/{id}
     *
     * @param id 유저 id
     * @return ResponseEntity<CustomResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     * @exception CustomException 해당 id를 가진 유저가 존재하지 않는다면 BAD_REQUEST 예외가 발생합니다
     */
    @Operation(summary = "유저 accessToken으로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<UserVo>> findById(@PathVariable Long id) {
        UserVo data = userService.findById(id);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 코코톡 id로 조회 API [GET] /user?cid=
     *
     * @param cid 코코톡 id
     * @return ResponseEntity<CustomResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     */
    @Operation(summary = "유저 cid로 조회")
    @GetMapping("/cid/{cocotalkId}")
    public ResponseEntity<CustomResponse<UserVo>> findByCid(@PathVariable String cocotalkId) {
        UserVo data = userService.findByCid(cocotalkId);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 email로 조회 API [GET] /user?email=
     *
     * @param address 유저 email
     * @return ResponseEntity<CustomResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     */
    @Operation(summary = "유저 email로 조회")
    @GetMapping("/email/{address}")
    public ResponseEntity<CustomResponse<UserVo>> findByEmail(@PathVariable String address) {
        UserVo data = userService.findByEmail(address);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 연락처로 조회 API [GET] /user?phone=[ string, ... ]
     *
     * @param phones 유저들의 연락처
     * @return ResponseEntity<CustomResponse<UserVo>> 조회된 유저 정보가 데이터에 포함됩니다.
     */
    @Operation(summary = "유저 연락처로 조회")
    @GetMapping("/phone")
    public ResponseEntity<CustomResponse<List<UserVo>>> findByPhone(@RequestParam List<String> phones) {
        List<UserVo> data = userService.findByPhones(phones);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }


    /**
     * 유저 정보 수정 API [PUT] /user/{id}
     *
     * @param request 수정할 내용을 담은 요청 모델
     * @return ResponseEntity<CustomResponse<UserVo>> 수정된 유저 정보가 데이터에 포함됩니다.
     */
    @Operation(summary = "유저 수정")
    @PutMapping
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<UserVo>> modify(@Parameter(hidden = true) User user, @RequestBody @Valid UserModifyRequest request) {
        UserVo data = userService.modify(user, request);
        return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }

    /**
     * 유저 삭제 API [DELETE] /user
     *
     * @return ResponseEntity<CustomResponse<String>> 삭제된 유저 cid를 포함한 메시지가 포함됩니다.
     */
    @Operation(summary = "유저 삭제")
    @DeleteMapping
    @SecurityRequirement(name = "X-ACCESS-TOKEN")
    public ResponseEntity<CustomResponse<String>> delete(@Parameter(hidden = true) User user) {
        String result = userService.delete(user);
        return new ResponseEntity<>(new CustomResponse<>(result), HttpStatus.OK);
    }
}
