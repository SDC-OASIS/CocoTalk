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

    @PostMapping
    public String sendMessage(@RequestParam("message") String message) {
        this.producer.sendMessage(message);
        return "success";
    }
}