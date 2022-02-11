package com.cocotalk.chat.service;

import com.cocotalk.chat.dto.kafka.ChatTopicDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper mapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

//    @KafkaListener(topics = "${spring.kafka.chat-topic}")
//    public void consume(String message) {
//        try{
//            ChatTopicDto chatTopicDto = mapper.readValue(message, ChatTopicDto.class);
//            //websocket 토픽에 메시지 전송
//            log.info(String.format("[Parse] Consumed message : %s", message));
//            simpMessagingTemplate.convertAndSend("/topic/" + chatTopicDto.getRoomId() + "/message", chatTopicDto.getMessageVo());
//        }catch (Exception e){
//            log.error("[consume] : json 파싱 실패");
//            e.printStackTrace();
//        }
//    }

    @KafkaListener(topics = "${spring.kafka.chat-topic}")
    public void consume(String message) {
        try{
            ChatTopicDto chatTopicDto = mapper.readValue(message, ChatTopicDto.class);
            //websocket 토픽에 메시지 전송
            log.info(String.format("[Parse] Consumed message : %s", message));
            simpMessagingTemplate.convertAndSend(chatTopicDto.getSend(), chatTopicDto.getPayload());
        }catch (Exception e){
            log.error("[consume] : json 파싱 실패");
            e.printStackTrace();
        }
    }
}