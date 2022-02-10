package com.cocotalk.presence.handler;

import com.cocotalk.presence.dto.PresenceRequest;
import com.cocotalk.presence.service.ChatConnectionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatConnectionService chatConnectionService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            PresenceRequest request = objectMapper.readValue(message.getPayload(), PresenceRequest.class);
            switch (request.getAction()) {
                case "register" : {
                    chatConnectionService.registerConnectionUrl(request.getServerUrl());
                    break;
                }
                case "withdraw" : {
                    chatConnectionService.withdrawConnectionUrl(request.getServerUrl());
                    break;
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection Closed");
    }
}
