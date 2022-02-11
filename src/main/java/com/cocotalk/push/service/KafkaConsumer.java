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

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final FCMService fcmService;
    private final ObjectMapper mapper;
    private final DeviceRepository deviceRepository;

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) throws IOException {
        PushTopicDto pushTopicDto = mapper.readValue(message, PushTopicDto.class);
        Flux<Device> devices =  deviceRepository.findByUserIdIn(pushTopicDto.getUserIdList());
        fcmService.sendByDevices(devices, pushTopicDto.getTitle(), pushTopicDto.getBody());
        System.out.println(String.format("Consumed message : %s", message));
        System.out.println(String.format("[Parse] Consumed message : %s", pushTopicDto));
    }

}