package com.cocotalk.chat.websocket.listener;

import com.cocotalk.chat.domain.entity.room.RoomMember;
import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final RoomService roomService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleBrokerAvailabilityListener(BrokerAvailabilityEvent event) {
        log.info("BrokerAvailabilityEvent - timestamp: " + event.getTimestamp());
        log.info("BrokerAvailabilityEvent - brokerAvailable: " + event.isBrokerAvailable());
        log.info("BrokerAvailabilityEvent - " + event.toString());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String view = accessor.getFirstNativeHeader("view");
        if (sessionAttributes != null && view != null) {
            sessionAttributes.put("view", view);
            if (view.equals("chatRoom")) {
                if (accessor.getFirstNativeHeader("roomId") != null &&
                        accessor.getFirstNativeHeader("userId") != null) {
                    ObjectId roomId = new ObjectId(accessor.getFirstNativeHeader("roomId"));
                    Long userId = Long.parseLong(accessor.getFirstNativeHeader("userId"));
                    sessionAttributes.put("roomId", roomId);
                    sessionAttributes.put("userId", userId);

                    RoomVo roomVo = roomService.saveEnteredAt(roomId, userId);
                    simpMessagingTemplate.convertAndSend("/topic/" + roomVo.getId() + "/room", roomVo);
                }
            } else if (view.equals("chatList")) {
                if (accessor.getFirstNativeHeader("userId") != null) {
                    Long userId = Long.parseLong(accessor.getFirstNativeHeader("userId"));
                    sessionAttributes.put("userId", userId);
                }
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.get("view") != null) {
            String view = sessionAttributes.get("view").toString();
            if (view.equals("chatRoom")) {
                String action = accessor.getFirstNativeHeader("action");
                if (action != null) { // 채팅방에서 나가기 버튼 눌렀을 때, 첫번째 Disconnect 일때
                    sessionAttributes.put("action", action);
                } else { // 두번째 Disconnect일 때
                    Long userId = (Long) sessionAttributes.get("userId");
                    ObjectId roomId = (ObjectId) sessionAttributes.get("roomId");
                    if (sessionAttributes.get("action") != null) {
                        if (sessionAttributes.get("action").equals("leave")) {
                            RoomVo roomVo = roomService.saveLeftAt(roomId, userId);
                            simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/room", roomVo);
                            int size = roomVo.getMembers().size();
                            for(int i = 0; i < size; ++i) {
                                long memberId = roomVo.getMembers().get(i).getUserId();
                                simpMessagingTemplate.convertAndSend("/topic/" + memberId + "/room", roomVo);
                            }
                        }
                    } else {
                        RoomVo roomVo = roomService.saveAwayAt(roomId, userId);
                        simpMessagingTemplate.convertAndSend("/topic/" + roomId + "/room", roomVo);
                    }
                }
            } else if (view.equals("chatList")) {
            }
        }
    }
}
