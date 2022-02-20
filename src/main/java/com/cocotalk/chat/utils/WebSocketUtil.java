package com.cocotalk.chat.utils;

import com.cocotalk.chat.dto.request.PresenceRequest;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class WebSocketUtil extends WebSocketClient implements ApplicationListener<ContextClosedEvent> {
    private final String stompEndpoint; // 클라이언트가 채팅을 위해 연결을 맺는 stomp 엔드포인트 
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static boolean reconnect = false;

    public WebSocketUtil(URI serverUri, Draft protocolDraft, String stompEndpoint) {
        super(serverUri, protocolDraft);
        this.stompEndpoint = stompEndpoint;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Opened connection to presence service");
        registerServer(); // 채팅 관리 서버에 자신의 stompEnpoint 등록
        if(reconnect) {
            executor.shutdownNow();
            reconnect = false;
        }
    }

    @Override
    public void onMessage(String message) {
        log.info("message : {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Closed connection to presence service");
        executor = Executors.newSingleThreadScheduledExecutor();
        reconnect = true;
        tryReconnect();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Withdraw server endpoint to Presence server");
        withdrawServer(); // 채팅 관리 서버에 자신의 stompEnpoint 등록 해제
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void tryReconnect() { // 채팅 관리 서버와 WebSocket 연결이 끊기면 10초마다 재연결 요청
        executor.schedule(this::reconnect, 10, TimeUnit.SECONDS);
    }

    public void registerServer() { // 채팅 관리 서버에 자신의 stompEnpoint 등록
        PresenceRequest registerRequest = PresenceRequest.builder()
                .action("register")
                .serverUrl(stompEndpoint)
                .build();
        try {
            String requestString = objectMapper.writeValueAsString(registerRequest);
            log.info(requestString);
            this.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[WebSocketUtil/registerServer] : 프리젠스 서버에게 URL 등록 메시지를 보내는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

    public void withdrawServer() { // 채팅 관리 서버에 자신의 stompEnpoint 등록 해제
        PresenceRequest withdrawRequest = PresenceRequest.builder()
                .action("withdraw")
                .serverUrl(stompEndpoint)
                .build();
        try {
            String requestString = objectMapper.writeValueAsString(withdrawRequest);
            log.info(requestString);
            this.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[WebSocketUtil/withdrawServer] : 프리젠스 서버에게 URL 해제 메시지를 보내는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }
}