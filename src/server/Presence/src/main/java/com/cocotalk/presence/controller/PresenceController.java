package com.cocotalk.presence.controller;

import com.cocotalk.presence.application.UserVo;
import com.cocotalk.presence.dto.response.CustomResponse;
import com.cocotalk.presence.service.ChatConnectService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PresenceController {
    private final ChatConnectService chatConnectService;

    @GetMapping("/stomp/connect")
    public ResponseEntity<CustomResponse<String>> handoverChatServerUrl(@Parameter(hidden = true) UserVo userVo) {
         String data = chatConnectService.getConnectionUrl();
         return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }
}
