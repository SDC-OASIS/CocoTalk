package com.cocotalk.presence.controller;

import com.cocotalk.presence.dto.response.CustomResponse;
import com.cocotalk.presence.exception.CustomError;
import com.cocotalk.presence.exception.CustomException;
import com.cocotalk.presence.service.ChatConnectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PresenceController {
    private final ChatConnectService chatConnectService;

    @GetMapping("/chatserver/connect")
    public ResponseEntity<CustomResponse<String>> handoverChatServerUrl() {
         String data = chatConnectService.handoverConnectionUrl();
         return new ResponseEntity<>(new CustomResponse<>(data), HttpStatus.OK);
    }
}
