package com.cocotalk.push.service;

import com.cocotalk.push.dto.kafka.PushTopicDto;
import com.cocotalk.push.entity.Device;
import com.cocotalk.push.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 *
 * Kafka의 push topic에서 push 요청을 기다리고 있다가,
 * 푸시 요청을 받으면 fcm을 이용해 client에게 push를 보냅니다.
 * push topic에 요청은 PushTopicDto 형태로 Chat 서버에서 보냅니다.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final FCMService fcmService;
    private final ObjectMapper mapper;
    private final DeviceRepository deviceRepository;

    /**
     *
     * Kafka의 push topic에 메시지가 들어오면 consume 함수가 실행됩니다.
     * 들어온 메시지를 ObjectMapper를 사용해 PushTopicDto객체로 매핑하여 사용합니다.
     *
     * @param message 들어온 메시지
     */
    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) throws IOException {
        PushTopicDto pushTopicDto = mapper.readValue(message, PushTopicDto.class);
        Flux<Device> devices =  deviceRepository.findByUserIdIn(pushTopicDto.getUserIdList());
        fcmService.sendByDevices(devices, pushTopicDto);
        System.out.println(String.format("Consumed message : %s", message));
        System.out.println(String.format("[Parse] Consumed message : %s", pushTopicDto));
    }
}