package com.cocotalk.chat.config;

import com.cocotalk.chat.utils.WebSocketUtil;
import org.java_websocket.drafts.Draft_6455;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class WebSocketConfig {
    @Value(value = "${oci.presence.websocket-url}")
    private String PRESENCE_SERVICE_WEBSOCKET_URL;

    @Bean
    public WebSocketUtil webSocketClient() throws Exception {
        URI uri = new URI(PRESENCE_SERVICE_WEBSOCKET_URL);
        WebSocketUtil webSocketUtil = new WebSocketUtil(uri, new Draft_6455());
        //웹소켓 커넥팅
        webSocketUtil.connectBlocking();

        return webSocketUtil;
    }
}