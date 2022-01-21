package com.cocotalk.chat.controller;

import com.cocotalk.chat.document.message.ChatMessage;
import com.cocotalk.chat.document.message.InviteMessage;
import com.cocotalk.chat.service.ChatMessageService;
import com.cocotalk.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @MessageMapping("/{roomId}/connect")
    public ChatMessage connect(@DestinationVariable String roomId,
                               StompHeaderAccessor headerAccessor,
                               @Payload ChatMessage connectMessage) {
        return connectMessage;
    }

    @MessageMapping("/{roomId}/send")
    @SendTo("/topic/{roomId}")
    public ChatMessage send(@DestinationVariable String roomId,
                            @Payload ChatMessage chatMessage) {
        // 페이로드를 바로 저장하는게 맞을까?
        chatMessageService.saveChatMessage(chatMessage);
        roomService.saveMessageId(roomId, chatMessage);
        return chatMessage;
    }

    @MessageMapping("/{roomId}/invite")
    @SendTo("/topic/{roomId}")
    public ChatMessage invite(@DestinationVariable String roomId,
                               @Payload InviteMessage inviteMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        chatMessageService.saveInviteMessage(inviteMessage);
        inviteMessage.getInvitees()
                        .forEach(userId -> headerAccessor.getSessionAttributes().put("userId", userId));
        roomService.invite(roomId, inviteMessage.getInvitees());
        return inviteMessage;
    }

    @MessageMapping("/{roomId}/leave")
    @SendTo("/topic/{roomId}")
    public ChatMessage leave(@DestinationVariable String roomId,
                            @Payload ChatMessage leaveMessage,
                            SimpMessageHeaderAccessor headerAccessor) {
        chatMessageService.saveChatMessage(leaveMessage);
        roomService.leave(roomId, leaveMessage.getUserId());
        headerAccessor.getSessionAttributes().remove("userId");
        return leaveMessage;
    }
}