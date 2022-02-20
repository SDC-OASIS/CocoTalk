package com.cocotalk.chat.config;

import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.utils.WebSocketUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft_6455;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {
    @Value("${server.port}")
    private int port;

    @Value(value = "${oci.presence.websocket-url}")
    private String PRESENCE_SERVICE_WEBSOCKET_URL; // 채팅 관리 서버 URL

    private final RestTemplate restTemplate = new RestTemplate();

    @Bean
    @DependsOn("ServerUrl")
    public WebSocketUtil webSocketClient() throws Exception { // 채팅 관리 서버와 WebSocket 연결
        URI uri = new URI(PRESENCE_SERVICE_WEBSOCKET_URL);
        WebSocketUtil webSocketUtil = new WebSocketUtil(uri, new Draft_6455(), ServerUrl());
        webSocketUtil.connectBlocking();
        return webSocketUtil;
    }

    @Bean
    public String ServerUrl() { // 자신의 Public IP와 포트 번호를 추출
        String publicIp = restTemplate.getForObject("http://checkip.amazonaws.com/", String.class);
        log.info("Response From http://checkip.amazonaws.com/ : {}", publicIp);
        if(publicIp == null) throw new CustomException(CustomError.COMMUNICATION, "채팅 서버의 Public IP 요청에 실패했습니다.");
        publicIp = publicIp.replaceAll("\n", "");
        return "http://" + publicIp + ":" + port + "/stomp";
    }
}