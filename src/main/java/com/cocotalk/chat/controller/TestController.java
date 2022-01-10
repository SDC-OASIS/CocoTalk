package com.cocotalk.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TestController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/chat")
    public String chatTest(){
        return "chat";
    }

    @GetMapping("/touser")
    public String toUserTest(){
        return "touser";
    }

    @GetMapping("/foo")
    public String foo() { return "foo"; }

    @GetMapping("/bar")
    public String bar() { return "bar"; }

//    @MessageMapping("/good")
//    public String handle(String message) {
//        return message + " - good";
//    }

    @MessageMapping("/good/{id}")
    public String handle(Message message, MessageHeaders messageHeaders,
                         MessageHeaderAccessor messageHeaderAccessor, SimpMessageHeaderAccessor simpMessageHeaderAccessor,
                         StompHeaderAccessor stompHeaderAccessor, @Payload String payload,
                         @Header("destination") String destination, @Headers Map<String, String> headers,
                         @DestinationVariable String id) {

        System.out.println("---- Message ----");
        System.out.println(message);

        System.out.println("---- MessageHeaders ----");
        System.out.println(messageHeaders);

        System.out.println("---- MessageHeaderAccessor ----");
        System.out.println(messageHeaderAccessor);

        System.out.println("---- SimpMessageHeaderAccessor ----");
        System.out.println(simpMessageHeaderAccessor);

        System.out.println("---- StompHeaderAccessor ----");
        System.out.println(stompHeaderAccessor);

        System.out.println("---- @Payload ----");
        System.out.println(payload);

        System.out.println("---- @Header(\"destination\") ----");
        System.out.println(destination);

        System.out.println("----  @Headers ----");
        System.out.println(headers);

        System.out.println("----  @DestinationVariable ----");
        System.out.println(id);

        return payload;
    }

    @GetMapping(path = "/greet")
    public String greet() {
        return "greet";
    }

    @PostMapping(path = "/greet")
    @ResponseBody
    public void greet(@RequestBody String greet) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        simpMessagingTemplate.convertAndSend("/topic/greet", "[" + now + "]" + greet);
    }

    @MessageMapping("/good")
    @SendToUser("/queue/something")
    public String handleSendToUser(@Payload String payload) {
        return payload;
    }
}