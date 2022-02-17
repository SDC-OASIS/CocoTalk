package com.cocotalk.chat.controller;

import com.cocotalk.chat.dto.request.CrashRequest;
import com.cocotalk.chat.service.kafka.KafkaProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "소켓 전송 API")
public class WebSocketController {
    private final KafkaProducer kafkaProducer;

    @PostMapping("/crash")
    @Operation(summary = "요청의 userId의 clientType이 일치하는 접속중인 기기 중 fcm token이 다른 기기 전부 로그아웃 요청")
    public void crash(@RequestBody CrashRequest request) {
        String type = request.getClientType().toLowerCase();
        if("mobile".equals(type) || "web".equals(type)){
            kafkaProducer.sendToChat("/topic/" + request.getUserId() + "/crash/" + type, request.getFcmToken());
            log.info("[crash/request] : " + "/topic/" + request.getUserId() + "/crash/" + type +" :  " +request.getFcmToken());
        }
    }
}
