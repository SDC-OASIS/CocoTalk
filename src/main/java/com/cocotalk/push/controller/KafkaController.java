package com.cocotalk.push.controller;

import com.cocotalk.push.service.KafkaConsumer;
import com.cocotalk.push.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/kafka")
public class KafkaController {
    private final KafkaProducer producer;
    private final KafkaConsumer consumer;


    @GetMapping
    public String listenMessage(@RequestParam("message") String message) {
        try {
            this.consumer.consume(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @PostMapping
    public String sendMessage(@RequestParam("message") String message) {
        this.producer.sendMessage(message);
        return "success";
    }
}