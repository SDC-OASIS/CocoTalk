package com.cocotalk.chat.utils;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class WebSocketUtils extends WebSocketClient {

    public WebSocketUtils(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Opened connection");
    }

    @Override
    public void onMessage(String message) {
        log.info("message : {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Closed connection");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}