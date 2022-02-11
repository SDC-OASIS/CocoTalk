package com.cocotalk.chat.controller;

import com.cocotalk.chat.domain.vo.*;
import com.cocotalk.chat.dto.request.ChatMessageRequest;
import com.cocotalk.chat.dto.request.InviteMessageRequest;
import com.cocotalk.chat.dto.request.RoomRequest;
import com.cocotalk.chat.service.KafkaProducer;
import com.cocotalk.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * STOMP 메시지를 받는 컨트롤러
 *
 * @author 황종훈
 * @version 1.0, 최초 작성
 * @see com.cocotalk.chat.service.RoomService
 * @see com.cocotalk.chat.service.KafkaProducer
 */

@Slf4j
@Controller
@RequiredArgsConstructor
@MessageMapping("/chatroom")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RoomService roomService;
    private final KafkaProducer kafkaProducer;

    @GetMapping
    public String index() {
        return "index";
    }

    /**
     * 채팅방 생성 /chatroom/new
     *
     * @param roomRequest 새로운 채팅방 생성을 요청하는 모델
     */
    @MessageMapping("/new")
    public void createRoom(@Payload RoomRequest roomRequest) {
        RoomVo roomVo = roomService.create(roomRequest); // (1) 메시지 수신 후 방 생성
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) { // (2) 방 생성 정보를 채팅방에 포함된 멤버들에게 pub
            Long userId = roomVo.getMembers().get(i).getUserId();
            kafkaProducer.sendToChat("/topic/" + userId + "/room/new", roomVo);
        }
    }

    /**
     * 메시지 전송 /chatroom/{roomId}/message/send
     *
     * @param roomId 메시지를 전송하는 채팅방의 ObjectId
     * @param chatMessageRequest 메시지 전송 요청 모델
     */
    @MessageMapping("/{roomId}/message/send")
    public void send(@DestinationVariable ObjectId roomId,
                            @Payload ChatMessageRequest chatMessageRequest) {
        MessageVo<ChatMessageVo> messageVo = roomService.saveChatMessage(roomId, chatMessageRequest);
        List<Long> receiverIds = chatMessageRequest.getReceiverIds();

        kafkaProducer.sendToChat("/topic/" + roomId + "/message", messageVo);

        for (Long userId : receiverIds) {
            kafkaProducer.sendToChat("/topic/" + userId + "/message", messageVo);
        }
        kafkaProducer.sendToPush(chatMessageRequest);
    }


    /**
     * 채팅방 초대 메시지 전송 /chatroom/{roomId}/message/invite
     *
     * @param roomId 메시지를 전송하는 채팅방의 ObjectId
     * @param inviteMessageRequest 초대 메시지 전송 요청 모델
     */
    @MessageMapping("/{roomId}/message/invite")
    public void invite(@DestinationVariable ObjectId roomId,
                                  @Payload InviteMessageRequest inviteMessageRequest) {
        MessageWithRoomVo<InviteMessageVo> messageWithRoomVo = roomService.saveInviteMessage(roomId, inviteMessageRequest);

        MessageVo<InviteMessageVo> inviteMessageVo = messageWithRoomVo.getMessageVo();
        RoomVo roomVo = messageWithRoomVo.getRoomVo();

        kafkaProducer.sendToChat("/topic/" + roomId + "/message", inviteMessageVo);
        kafkaProducer.sendToChat("/topic/" + roomId + "/room", roomVo);
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) {
            Long userId = roomVo.getMembers().get(i).getUserId();
            kafkaProducer.sendToChat("/topic/" + userId + "/room/new", roomVo);
        }
    }

    /**
     * 채팅방 나가기 메시지 전송 /chatroom/{roomId}/message/leave
     *
     * @param roomId 메시지를 전송하는 채팅방의 ObjectId
     * @param leaveMessageRequest 나가기 메시지 전송 요청 모델
     */
    @MessageMapping("/{roomId}/message/leave")
    public void leave(@DestinationVariable ObjectId roomId,
                               @Payload ChatMessageRequest leaveMessageRequest,
                               SimpMessageHeaderAccessor headerAccessor) {
        MessageWithRoomVo<ChatMessageVo> messageWithRoomVo = roomService.saveLeaveMessage(roomId, leaveMessageRequest);
        MessageVo<ChatMessageVo> messageVo = messageWithRoomVo.getMessageVo();
        RoomVo roomVo = messageWithRoomVo.getRoomVo();

        kafkaProducer.sendToChat("/topic/" + roomId + "/message", messageVo);
        kafkaProducer.sendToChat("/topic/" + roomId + "/room", roomVo);
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) {
            Long userId = roomVo.getMembers().get(i).getUserId();
            kafkaProducer.sendToChat("/topic/" + userId + "/room/new", roomVo);
        }
    }

    /**
     * 채팅방 깨우기 메시지 전송 /chatroom/{roomId}/message/awake
     *
     * @param roomId 메시지를 전송하는 채팅방의 ObjectId
     * @param awakeMessageRequest 깨우기 메시지 전송 요청 모델
     */
    @MessageMapping("/{roomId}/message/awake")
    public void awake(@DestinationVariable ObjectId roomId,
                      @Payload ChatMessageRequest awakeMessageRequest) {
        MessageWithRoomVo<ChatMessageVo> messageWithRoomVo = roomService.saveAwakeMessage(roomId, awakeMessageRequest);
        MessageVo<ChatMessageVo> messageVo = messageWithRoomVo.getMessageVo();
        RoomVo roomVo = messageWithRoomVo.getRoomVo();

        kafkaProducer.sendToChat("/topic/" + roomId + "/message", messageVo);
        kafkaProducer.sendToChat("/topic/" + roomId + "/room", roomVo);
        int size = roomVo.getMembers().size();
        for(int i = 0; i < size; ++i) {
            Long userId = roomVo.getMembers().get(i).getUserId();
            kafkaProducer.sendToChat("/topic/" + userId + "/room/new", roomVo);
        }
    }
}