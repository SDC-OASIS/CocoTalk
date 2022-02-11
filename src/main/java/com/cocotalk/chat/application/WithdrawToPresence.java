package com.cocotalk.chat.application;

import com.cocotalk.chat.dto.request.PresenceRequest;
import com.cocotalk.chat.utils.WebSocketUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawToPresence implements ApplicationListener<ContextClosedEvent> {
    @Value("${server.port}")
    private int port;

    private final WebSocketUtils webSocketUtils;
    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        String publicIp = restTemplate.getForObject("http://checkip.amazonaws.com/", String.class); // public ip
        publicIp = publicIp.replaceAll("\n", "");
        String chatServerURL = publicIp + ":" + port + "/stomp";

        PresenceRequest withdrawRequest = PresenceRequest.builder()
                .action("withdraw")
                .serverUrl(chatServerURL)
                .build();

        try {
            String requestString = objectMapper.writeValueAsString(withdrawRequest);
            log.info(requestString);
            webSocketUtils.send(requestString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
