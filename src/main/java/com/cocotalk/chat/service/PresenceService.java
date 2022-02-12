package com.cocotalk.chat.service;

import com.cocotalk.chat.config.ServerUrlConfig;
import com.cocotalk.chat.dto.request.PresenceRequest;
import com.cocotalk.chat.utils.WebSocketUtils;
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
    private final WebSocketUtils webSocketUtils;
    private final ServerUrlConfig serverUrlConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String serverUrl = serverUrlConfig.ServerUrl();
        PresenceRequest registerRequest = PresenceRequest.builder()
                .action("register")
                .serverUrl(serverUrl)
                .build();
        try {
            String requestString = objectMapper.writeValueAsString(registerRequest);
            log.info(requestString);
            webSocketUtils.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
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
            webSocketUtils.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
