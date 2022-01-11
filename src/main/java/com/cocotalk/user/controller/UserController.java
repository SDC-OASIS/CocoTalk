package com.cocotalk.user.controller;


import com.cocotalk.user.dto.response.UserResponse;
import com.cocotalk.user.mapper.UserMapper;
import com.cocotalk.user.service.UserService;
import com.cocotalk.user.support.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<UserResponse> data = userService.getAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new GlobalResponse<>(data), HttpStatus.OK);
    }
}
