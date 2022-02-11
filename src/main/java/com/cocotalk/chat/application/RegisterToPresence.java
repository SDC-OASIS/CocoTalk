package com.cocotalk.chat.application;

import com.cocotalk.chat.dto.request.PresenceRequest;
import com.cocotalk.chat.utils.WebSocketUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterToPresence implements ApplicationRunner {
    @Value("${server.port}")
    private int port;

    private final WebSocketUtils webSocketUtils;
    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public void run(ApplicationArguments args) {
        String publicIp = restTemplate.getForObject("http://checkip.amazonaws.com/", String.class); // public ip
        publicIp = publicIp.replaceAll("\n", "");
        String chatServerURL = publicIp + ":" + port + "/stomp";

        PresenceRequest registerRequest = PresenceRequest.builder()
                .action("register")
                .serverUrl(chatServerURL)
                .build();

        try {
            String requestString = objectMapper.writeValueAsString(registerRequest);
            log.info(requestString);
            webSocketUtils.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
