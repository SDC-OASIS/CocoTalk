package com.cocotalk.chat.service.kafka;

import com.cocotalk.chat.domain.entity.room.RoomType;
import com.cocotalk.chat.dto.kafka.ChatTopicDto;
import com.cocotalk.chat.dto.kafka.PushTopicDto;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 김민정
 */
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
            e.printStackTrace();
            log.error("[KafkaProducer/sendToChat] : 메시지를 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

    public void sendToPush(ChatMessageRequest request) {
        //자기 자신이 한 채팅에는 push를 보내지 않음
        List<Long> pushReceivers = new LinkedList<>(request.getReceiverIds());
        pushReceivers.remove(request.getUserId());

        PushTopicDto pushTopicDto = PushTopicDto.builder()
                .userIdList(pushReceivers)
                .roomId(request.getRoomId().toString())
                .roomname(request.getRoomname())
                .message(request.getContent())
                .roomType(RoomType.values()[request.getRoomType()])
                .username(request.getUsername())
                .build();
        try {
            kafkaTemplate.send(pushTopic, mapper.writeValueAsString(pushTopicDto));
            log.info(String.format("Produce message ("+pushTopic+") : %s", pushTopicDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("[KafkaProducer/sendToPush] : 메시지를 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

}