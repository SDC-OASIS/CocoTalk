package com.cocotalk.chat.controller;

import com.cocotalk.chat.document.ChatMessage;
import com.cocotalk.chat.model.vo.ChatMessageVo;
import com.cocotalk.chat.service.ChatMessageService;
import com.cocotalk.chat.service.RoomService;
import com.cocotalk.chat.utils.mapper.ChatMessageMapper;
import com.cocotalk.chat.utils.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatMessageMapper chatMessageMapper;
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @GetMapping
    public String index() {
        return "index";
    }

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage sendMessage(@Payload ChatMessageVo chatMessageVo,
                                   @PathVariable String roomId) {
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageVo);
        chatMessageService.save(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               @PathVariable String roomId,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("userId", chatMessage.getUserId());
        return chatMessage;
    }

    @MessageMapping("/good/{id}")
    public String handle(Message message, MessageHeaders messageHeaders,
                         MessageHeaderAccessor messageHeaderAccessor, SimpMessageHeaderAccessor simpMessageHeaderAccessor,
                         StompHeaderAccessor stompHeaderAccessor, @Payload String payload,
                         @Header("destination") String destination, @Headers Map<String, String> headers,
                         @DestinationVariable String id) {

        System.out.println("---- ChatMessage ----");
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
}