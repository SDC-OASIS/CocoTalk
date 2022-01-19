package com.cocotalk.chat.controller;

import com.cocotalk.chat.document.message.ChatMessage;
import com.cocotalk.chat.document.message.InviteMessage;
import com.cocotalk.chat.service.ChatMessageService;
import com.cocotalk.chat.service.RoomService;
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

import java.util.Map;

@Controller
@RequiredArgsConstructor
@MessageMapping("/chat")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    private final RoomService roomService;

    @GetMapping
    public String index() {
        return "index";
    }

    @MessageMapping("/{roomId}/send")
    @SendTo("/topic/{roomId}")
    public ChatMessage send(@DestinationVariable String roomId,
                            @Payload ChatMessage chatMessage) {
        // 페이로드를 바로 저장하는게 맞을까?
        chatMessageService.save(chatMessage);
        roomService.saveMessageId(roomId, chatMessage.getId());
        return chatMessage;
    }

    @MessageMapping("/{roomId}/invite")
    @SendTo("/topic/{roomId}")
    public ChatMessage invite(@DestinationVariable String roomId,
                               @Payload InviteMessage inviteMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        inviteMessage.getInvitees()
                        .forEach(userId -> headerAccessor.getSessionAttributes().put("userId", userId));
        roomService.invite(roomId, inviteMessage.getInvitees());
        return inviteMessage;
    }

    @MessageMapping("/{roomId}/leave")
    @SendTo("/topic/{roomId}")
    public ChatMessage leave(@DestinationVariable String roomId,
                            @Payload ChatMessage chatMessage,
                            SimpMessageHeaderAccessor headerAccessor) {
        roomService.leave(roomId, chatMessage.getUserId());
        headerAccessor.getSessionAttributes().remove("userId");
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