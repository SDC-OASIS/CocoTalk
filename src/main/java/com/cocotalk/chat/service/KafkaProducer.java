package com.cocotalk.chat.service;

import com.cocotalk.chat.dto.kafka.ChatTopicDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    @Value("${spring.kafka.push-topic}")
    private String pushTopic;

    @Value("${spring.kafka.chat-topic}")
    private String chatTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;


    public void sendToChat(String send, Object payload) {
        ChatTopicDto chatTopicDto = ChatTopicDto.builder()
                .send(send)
                .payload(payload)
                .build();
        try {
            kafkaTemplate.send(chatTopic, mapper.writeValueAsString(chatTopicDto));
            log.info(String.format("Produce message ("+chatTopic+") : %s", mapper.writeValueAsString(chatTopicDto)));
        } catch (JsonProcessingException e) {
            log.error("[KafkaProducer] : json 변환 실패");
            e.printStackTrace();
        }
    }

}