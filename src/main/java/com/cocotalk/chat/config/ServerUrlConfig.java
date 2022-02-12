package com.cocotalk.chat.config;

import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class ServerUrlConfig {
    @Value("${server.port}")
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Bean
    public String ServerUrl() {
        String publicIp = restTemplate.getForObject("http://checkip.amazonaws.com/", String.class); // public ip
        log.info("Response From http://checkip.amazonaws.com/ : {}", publicIp);
        if(publicIp == null) throw new CustomException(CustomError.COMMUNICATION, "채팅 서버의 Public IP 요청에 실패했습니다.");
        publicIp = publicIp.replaceAll("\n", "");
        return "http://" + publicIp + ":" + port + "/stomp";
    }
}
