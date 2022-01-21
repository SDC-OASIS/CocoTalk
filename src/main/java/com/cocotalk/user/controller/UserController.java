package com.cocotalk.user.controller;


import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.dto.request.UserModifyRequest;
import com.cocotalk.user.dto.response.UserResponse;
import com.cocotalk.user.mapper.UserMapper;
import com.cocotalk.user.service.UserService;
import com.cocotalk.user.support.GlobalResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "유저 관련 API")
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @ApiOperation(value = "유저 전체 조회")
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<UserResponse> data = userService.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }

    @ApiOperation(value = "유저 id로 조회")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return new ResponseEntity<>(new GlobalResponse<>(userMapper.toDto(user)), HttpStatus.OK);
    }

    @ApiOperation(value = "유저 id로 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyById(@PathVariable Long id, @RequestBody UserModifyRequest request) {
        User modifiedUser = userService.modifyById(id, request);
        return new ResponseEntity<>(new GlobalResponse<>(userMapper.toDto(modifiedUser)), HttpStatus.OK);
    }

    @ApiOperation(value = "유저 id로 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        String cid = userService.deleteById(id);
        String message = String.format("유저 %s이 삭제되었습니다.", cid);
        return new ResponseEntity<>(new GlobalResponse<>(message), HttpStatus.NO_CONTENT);
    }
}
