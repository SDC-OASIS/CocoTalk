package com.cocotalk.chat.utils;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.*;

@Slf4j
public class WebSocketUtils extends WebSocketClient {
    private static final RestTemplate restTemplate = new RestTemplate();

    public WebSocketUtils(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Opened connection to presence service");
    }

    @Override
    public void onMessage(String message) {
        log.info("message : {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Closed connection to presence service");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void registerURL(String url){
        this.send(url);
    }
}