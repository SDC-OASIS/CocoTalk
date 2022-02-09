package com.cocotalk.chat.controller;

import com.cocotalk.chat.domain.vo.*;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.dto.request.InviteMessageRequest;
import com.cocotalk.chat.dto.request.RoomRequest;
import com.cocotalk.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@MessageMapping("/chatroom")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RoomService roomService;

    @GetMapping
    public String index() {
        return "index";
    }

    @MessageMapping("/new")
    public void createRoom(@Payload RoomRequest roomRequest) {
        RoomVo roomVo = roomService.create(roomRequest);
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) {
            long userId = roomVo.getMembers().get(i).getUserId();
            simpMessagingTemplate.convertAndSend("/topic/" + userId + "/room/new", roomVo);
        }
    }

    @MessageMapping("/{roomId}/message/send")
    public void send(@DestinationVariable ObjectId roomId,
                     @Payload ChatMessageRequest chatMessageRequest) {
        MessageVo<ChatMessageVo> messageVo = roomService.saveChatMessage(roomId, chatMessageRequest);
        List<Long> receivers = chatMessageRequest.getReceiverIds();
        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/message", messageVo);
        for(int i = 0; i < receivers.size(); ++i) {
            long userId = receivers.get(i);
            simpMessagingTemplate.convertAndSend("/topic/" + userId + "/messageVo", messageVo);
        }
    }

    @MessageMapping("/{roomId}/message/invite")
    public void invite(@DestinationVariable ObjectId roomId,
                                  @Payload InviteMessageRequest inviteMessageRequest) {
        MessageWithRoomVo<InviteMessageVo> messageWithRoomVo = roomService.saveInviteMessage(roomId, inviteMessageRequest);

        MessageVo<InviteMessageVo> inviteMessageVo = messageWithRoomVo.getMessageVo();
        RoomVo roomVo = messageWithRoomVo.getRoomVo();

        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/message", inviteMessageVo);
        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/room", roomVo);
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) {
            long userId = roomVo.getMembers().get(i).getUserId();
            simpMessagingTemplate.convertAndSend("/topic/" + userId + "/room/new", roomVo);
        }
    }

    @MessageMapping("/{roomId}/message/leave")
    public void leave(@DestinationVariable ObjectId roomId,
                               @Payload ChatMessageRequest leaveMessageRequest,
                               SimpMessageHeaderAccessor headerAccessor) {
        MessageWithRoomVo<ChatMessageVo> messageWithRoomVo = roomService.saveLeaveMessage(roomId, leaveMessageRequest);
        MessageVo<ChatMessageVo> messageVo = messageWithRoomVo.getMessageVo();
        RoomVo roomVo = messageWithRoomVo.getRoomVo();

        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/message", messageVo);
        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/room", roomVo);
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) {
            long userId = roomVo.getMembers().get(i).getUserId();
            simpMessagingTemplate.convertAndSend("/topic/" + userId + "/room/new", roomVo);
        }
    }

    @MessageMapping("/{roomId}/message/awake")
    public void awake(@DestinationVariable ObjectId roomId,
                      @Payload ChatMessageRequest awakeMessageRequest) {
        MessageWithRoomVo<ChatMessageVo> messageWithRoomVo = roomService.saveAwakeMessage(roomId, awakeMessageRequest);
        MessageVo<ChatMessageVo> messageVo = messageWithRoomVo.getMessageVo();
        RoomVo roomVo = messageWithRoomVo.getRoomVo();

        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/message", messageVo);
        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/room", roomVo);
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) {
            long userId = roomVo.getMembers().get(i).getUserId();
            simpMessagingTemplate.convertAndSend("/topic/" + userId + "/room/new", roomVo);
        }
    }
}