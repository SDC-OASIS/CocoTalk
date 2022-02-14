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
    private final String stompEndpoint;
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
        registerServer();
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
        withdrawServer();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void tryReconnect() {
        executor.schedule(() -> {
            try {
                log.info("Try reconnect every 10 seconds");
                this.reconnectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.SECONDS);
    }

    public void registerServer() {
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
            log.error("[PresenceService/run] : 프리젠스 서버에게 URL 등록 메시지를 보내는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

    public void withdrawServer() { // 서버 종료 시
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
            log.error("[PresenceService/onApplicationEvent] : 프리젠스 서버에게 URL 해제 메시지를 보내는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }
}