package com.cocotalk.chat.utils;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class WebSocketUtil extends WebSocketClient {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    // private static ScheduledFuture<?> task;

    public WebSocketUtil(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Opened connection to presence service");
        // task.cancel(false);
    }

    @Override
    public void onMessage(String message) {
        log.info("message : {}", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Closed connection to presence service");
        // tryReconnect();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

//    public void tryReconnect() {
//         task = executor.scheduleAtFixedRate(() -> {
//            try {
//                log.info("Try reconnect per 5 seconds");
//                this.reconnectBlocking();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, 0, 50000, TimeUnit.MILLISECONDS);
//    }
}