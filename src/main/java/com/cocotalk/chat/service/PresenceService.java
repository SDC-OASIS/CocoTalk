package com.cocotalk.chat.service;

import com.cocotalk.chat.config.ServerUrlConfig;
import com.cocotalk.chat.dto.request.PresenceRequest;
import com.cocotalk.chat.utils.WebSocketUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceService implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {
    private final ObjectMapper objectMapper;
    private final WebSocketUtil webSocketUtil;
    private final ServerUrlConfig serverUrlConfig;

    @Override
    public void run(ApplicationArguments args) {
        String serverUrl = serverUrlConfig.ServerUrl();
        PresenceRequest registerRequest = PresenceRequest.builder()
                .action("register")
                .serverUrl(serverUrl)
                .build();
        try {
            String requestString = objectMapper.writeValueAsString(registerRequest);
            log.info(requestString);
            webSocketUtil.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            log.error("[PresenceService/run] : 프리젠스 서버에게 URL 등록 메시지를 보내는 도중 문제가 발생했습니다.");
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        String serverUrl = serverUrlConfig.ServerUrl();
        PresenceRequest withdrawRequest = PresenceRequest.builder()
                .action("withdraw")
                .serverUrl(serverUrl)
                .build();
        try {
            String requestString = objectMapper.writeValueAsString(withdrawRequest);
            log.info(requestString);
            webSocketUtil.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            log.error("[PresenceService/onApplicationEvent] : 프리젠스 서버에게 URL 해제 메시지를 보내는 도중 문제가 발생했습니다.");
        }
    }
}
