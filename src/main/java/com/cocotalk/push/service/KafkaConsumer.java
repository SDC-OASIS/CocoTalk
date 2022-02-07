package com.cocotalk.push.service;

import com.cocotalk.push.dto.push.PushInfoInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) throws IOException {
        PushInfoInput pushInfoInput = mapper.readValue(message, PushInfoInput.class);
        System.out.println(String.format("Consumed message : %s", pushInfoInput));
    }

}