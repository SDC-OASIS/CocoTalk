package com.cocotalk.chat.controller;

import com.cocotalk.chat.dto.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@MessageMapping("/chatlist")
public class ChatListController {

    @MessageMapping("/{userId}/send")
    @SendTo("/topic/{userId}")
    public ChatMessageRequest send(@DestinationVariable Long userId,
                                   @Payload ChatMessageRequest chatMessageRequest) {
        return chatMessageRequest;
    }
}
