package com.cocotalk.chat.controller;

import com.cocotalk.chat.dto.request.CrashRequest;
import com.cocotalk.chat.service.kafka.KafkaProducer;
import com.cocotalk.chat.utils.WebSocketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketController {
    private final WebSocketUtils websocketUtil;
    private final KafkaProducer kafkaProducer;

    @PostMapping("/crash")
    public void crash(@RequestBody CrashRequest request) {
        String type = request.getClientType().toLowerCase();
        if("mobile".equals(type) || "web".equals(type)){
            kafkaProducer.sendToChat("/topic/" + request.getUserId() + "/crash/" + type, request.getFcmToken());
            log.info("[crash/request] : " + "/topic/" + request.getUserId() + "/crash/" + type +" :  " +request.getFcmToken());
        }
    }

}
