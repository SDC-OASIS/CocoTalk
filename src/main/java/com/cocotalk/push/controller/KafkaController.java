package com.cocotalk.push.controller;

import com.cocotalk.push.dto.push.PushMessage;
import com.cocotalk.push.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/*
*
* 
* sendMessage 함수를 사용하기 위해 만든 테스트용 컨트롤러입니다.
* 필요하신 부분에서 바로 sendMessage 함수를 사용해주세요.
* 
* 
 */
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

    @PostMapping("/object")
    public String sendPushMessage(@RequestBody @Valid PushMessage pushMessage) {
        this.producer.sendPushMessage(pushMessage);
        return "success";
    }

}