package com.cocotalk.chat.websocket.listener;

import com.cocotalk.chat.domain.entity.message.ChatMessage;
import com.cocotalk.chat.domain.entity.message.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleBrokerAvailabilityListener(BrokerAvailabilityEvent event) {
        log.info("BrokerAvailabilityEvent - timestamp: " + event.getTimestamp());
        log.info("BrokerAvailabilityEvent - brokerAvailable: " + event.isBrokerAvailable());
        log.info("BrokerAvailabilityEvent - " + event.toString());
    }


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        List<String> uiList = accessor.getNativeHeader("userId");
//        List<String> riList = accessor.getNativeHeader("roomId");
//        if(uiList != null && riList != null) {
//            Long userId = Long.parseLong(uiList.get(0));
//            ObjectId roomId = new ObjectId(riList.get(0));
//            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
//            if(sessionAttributes != null) {
//                accessor.getSessionAttributes().put(accessor.getSessionId(), new SessionVo(userId, roomId));
//            }
//        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if(sessionAttributes != null) {
            String view = sessionAttributes.get("view").toString();
            if(view.equals("chatroom")) {
                Long userId = Long.parseLong(sessionAttributes.get("userId").toString());
                ObjectId roomId = new ObjectId(sessionAttributes.get("roomId").toString());
                log.info("User Disconnected - userId = " + userId);
                ChatMessage chatMessage = ChatMessage.builder()
                        .userId(userId)
                        .roomId(roomId)
                        .type(MessageType.AWAY.ordinal())
                        .sentAt(LocalDateTime.now())
                        .build();

                messagingTemplate.convertAndSend("/topic/" + roomId, chatMessage);
            } else if (view.equals("main")) {
                // 메인화면 글로벌 소켓 예정
            }
        }
    }
}
