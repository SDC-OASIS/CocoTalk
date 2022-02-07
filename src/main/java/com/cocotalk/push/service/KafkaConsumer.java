package com.cocotalk.push.service;

import com.cocotalk.push.dto.kafka.PushMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final FCMService fcmService;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) throws IOException {
        PushMessage pushMessage = mapper.readValue(message, PushMessage.class);
        fcmService.sendByTokenList(pushMessage.getTokenList(), pushMessage.getTitle(), pushMessage.getBody());
        System.out.println(String.format("Consumed message : %s", message));
        System.out.println(String.format("[Parse] Consumed message : %s", pushMessage));
    }

}