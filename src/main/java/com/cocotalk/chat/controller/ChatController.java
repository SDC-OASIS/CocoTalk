package com.cocotalk.chat.controller;

import com.cocotalk.chat.domain.vo.ChatMessageVo;
import com.cocotalk.chat.domain.vo.InviteMessageVo;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.dto.request.InviteMessageRequest;
import com.cocotalk.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RoomService roomService;

    @GetMapping
    public String index() {
        return "index";
    }

    @MessageMapping("/{roomId}/send")
    @SendTo("/topic/{roomId}")
    public ChatMessageVo send(@DestinationVariable ObjectId roomId,
                            @Payload ChatMessageRequest chatMessageRequest) {
        ChatMessageVo chatMessageVo = roomService.saveChatMessage(roomId, chatMessageRequest);
        return chatMessageVo;
    }

    @MessageMapping("/{roomId}/invite")
    @SendTo("/topic/{roomId}")
    public InviteMessageVo invite(@DestinationVariable ObjectId roomId,
                                  @Payload InviteMessageRequest inviteMessageRequest,
                                  SimpMessageHeaderAccessor headerAccessor) {
        InviteMessageVo inviteMessageVo = roomService.saveInviteMessage(roomId, inviteMessageRequest);
        inviteMessageVo.getInviteeIds().forEach(userId -> headerAccessor.getSessionAttributes().put("userId", userId));
        return inviteMessageVo;
    }

    @MessageMapping("/{roomId}/leave")
    @SendTo("/topic/{roomId}")
    public ChatMessageVo leave(@DestinationVariable ObjectId roomId,
                               @Payload ChatMessageRequest leaveMessageRequest,
                               SimpMessageHeaderAccessor headerAccessor) {
        ChatMessageVo leaveMessageVo = roomService.saveLeaveMessage(roomId, leaveMessageRequest);
        headerAccessor.getSessionAttributes().remove("userId");
        return leaveMessageVo;
    }
}