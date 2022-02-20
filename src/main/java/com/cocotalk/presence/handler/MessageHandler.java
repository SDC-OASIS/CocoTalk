package com.cocotalk.presence.handler;

import com.cocotalk.presence.dto.request.PresenceRequest;
import com.cocotalk.presence.exception.CustomError;
import com.cocotalk.presence.exception.CustomException;
import com.cocotalk.presence.service.ChatConnectService;
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
    private final ChatConnectService chatConnectService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) { // 채팅 서버로부터 수신한 웹 소켓 메시지를 핸들링하는 메서드입니다.
        try {
            PresenceRequest request = objectMapper.readValue(message.getPayload(), PresenceRequest.class);
            switch (request.getAction()) {
                case "register" : { // (1) 채팅 서버 기동 시 URL 등록 메시지
                    chatConnectService.registerConnectionUrl(request.getServerUrl());
                    break;
                }
                case "withdraw" : { // (2) 채팅 서버 종료 시 URL 삭제 메시지
                    chatConnectService.withdrawConnectionUrl(request.getServerUrl());
                    break;
                }
                case "connect" : { // (3) 클라이어트 STOMP Connect 시 연결 정보 업데이트 메시지
                    chatConnectService.connectChatServer(request.getServerUrl());
                    break;
                }
                case "disconnect" : { // (4) 클라이어트 STOMP Disconnect 시 연결 정보 업데이트 메시지
                    chatConnectService.disconnectChatServer(request.getServerUrl());
                    break;
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("[MessageHandler/handleTextMessage] : 채팅 서버로부터 수신한 WebSocket 메시지를 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.BAD_REQUEST, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection Closed");
    }
}
