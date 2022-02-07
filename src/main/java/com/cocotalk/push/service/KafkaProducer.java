package com.cocotalk.push.service;

import com.cocotalk.push.dto.push.PushMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        System.out.println(String.format("Produce message ("+topic+") : %s", message));
        this.kafkaTemplate.send(topic, message);
    }

    public void sendPushMessage(PushMessage pushMessage) {
        System.out.println(String.format("Produce message ("+topic+") : %s", pushMessage));
        try {
            kafkaTemplate.send(topic, mapper.writeValueAsString(pushMessage));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}