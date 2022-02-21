package com.cocotalk.chat.service.kafka;

import com.cocotalk.chat.dto.kafka.ChatTopicDto;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 김민정
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = "${spring.kafka.chat-topic}")
    public void consume(String message) {
        try {
            ChatTopicDto chatTopicDto = objectMapper.readValue(message, ChatTopicDto.class);
            // websocket 토픽에 메시지 전송
            // log.info(String.format("[Parse] Consumed message : %s", message));
            simpMessagingTemplate.convertAndSend(chatTopicDto.getSend(), chatTopicDto.getPayload());
        } catch (Exception e){
            e.printStackTrace();
            log.error("[KafkaConsumer/consume] : 메시지를 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }
}