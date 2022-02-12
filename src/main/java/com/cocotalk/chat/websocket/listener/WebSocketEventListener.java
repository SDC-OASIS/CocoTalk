package com.cocotalk.chat.websocket.listener;

import com.cocotalk.chat.config.ServerUrlConfig;
import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.dto.request.PresenceRequest;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.service.KafkaProducer;
import com.cocotalk.chat.service.RoomService;
import com.cocotalk.chat.utils.WebSocketUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final RoomService roomService;
    private final ServerUrlConfig serverUrlConfig;
    private final ObjectMapper objectMapper;
    private final WebSocketUtils webSocketUtils;
    private final KafkaProducer kafkaProducer;

    @EventListener
    public void handleBrokerAvailabilityListener(BrokerAvailabilityEvent event) {
        log.info("BrokerAvailabilityEvent - timestamp: " + event.getTimestamp());
        log.info("BrokerAvailabilityEvent - brokerAvailable: " + event.isBrokerAvailable());
        log.info("BrokerAvailabilityEvent - " + event.toString());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        handleWebSocketConnectSession(accessor);
        PresenceRequest connectRequest = PresenceRequest.builder()
                .action("connect")
                .serverUrl(serverUrlConfig.ServerUrl())
                .build();
        try {
            webSocketUtils.send(objectMapper.writeValueAsString(connectRequest));
            log.info("sessionId = {}, {}에 연결됨.", accessor.getSessionId(), serverUrlConfig);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new CustomException(CustomError.JSON_PARSE, "프리젠스 서버에 connect 요청을 보내는 데 실패했습니다.");
        }
    }

    public void handleWebSocketConnectSession(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String view = accessor.getFirstNativeHeader("view");
        if (view != null && sessionAttributes != null) {
            sessionAttributes.put("view", view);
            if (view.equals("chatRoom")) {
                try {
                    ObjectId roomId = new ObjectId(Objects.requireNonNull(accessor.getFirstNativeHeader("roomId")));
                    Long userId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("userId")));
                    sessionAttributes.put("roomId", roomId);
                    sessionAttributes.put("userId", userId);

                    RoomVo roomVo = roomService.saveEnteredAt(roomId, userId);
                    kafkaProducer.sendToChat("/topic/" + roomVo.getId() + "/room", roomVo);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    throw new CustomException(CustomError.BAD_REQUEST, "WebSocket ChatRoom Connect 헤더에 roomId 또는 userId가 설정되지 않았습니다.");
                }
            } else if (view.equals("chatList")) {
                try {
                    Long userId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("userId")));
                    sessionAttributes.put("userId", userId);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    throw new CustomException(CustomError.BAD_REQUEST, "WebSocket ChatList Connect 헤더에 userId가 설정되지 않았습니다.");
                }
            }
        } else {
            throw new CustomException(CustomError.BAD_REQUEST, "WebSocket Connect 헤더에 view가 설정되지 않았습니다.");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        handleWebSocketDisconnectSession(accessor);
        PresenceRequest disconnectRequest = PresenceRequest.builder()
                .action("disconnect")
                .serverUrl(serverUrlConfig.ServerUrl())
                .build();
        try {
            webSocketUtils.send(objectMapper.writeValueAsString(disconnectRequest));
            log.info("sessionId = {}, {}과 연결 끊어짐.", accessor.getSessionId(), serverUrlConfig);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new CustomException(CustomError.JSON_PARSE, "프리젠스 서버에 disconnect 요청을 보내는 데 실패했습니다.");
        }
    }

    public void handleWebSocketDisconnectSession(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.get("view") != null) {
            String view = sessionAttributes.get("view").toString();
            if (view.equals("chatRoom")) {
                String action = accessor.getFirstNativeHeader("action");
                if (action != null) { // 채팅방에서 나가기 버튼 눌렀을 때
                    sessionAttributes.put("action", action);
                } else { // 단순 Disconnect일 때
                    Long userId = (Long) sessionAttributes.get("userId");
                    ObjectId roomId = (ObjectId) sessionAttributes.get("roomId");
                    if (sessionAttributes.get("action") != null) {
                        if (sessionAttributes.get("action").equals("leave")) {
                            RoomVo roomVo = roomService.saveLeftAt(roomId, userId);
                            kafkaProducer.sendToChat("/topic/" + roomId + "/room", roomVo);
                            int size = roomVo.getMembers().size();
                            for(int i = 0; i < size; ++i) {
                                long memberId = roomVo.getMembers().get(i).getUserId();
                                kafkaProducer.sendToChat("/topic/" + memberId + "/room", roomVo);
                            }
                        } else {
                            throw new CustomException(CustomError.UNKNOWN, "지원하지 않는 형식의 action 입니다.");
                        }
                    } else {
                        RoomVo roomVo = roomService.saveAwayAt(roomId, userId);
                        kafkaProducer.sendToChat("/topic/" + roomId + "/room", roomVo);
                    }
                }
            } else if (view.equals("chatList")) {
            }
        } else {
            throw new CustomException(CustomError.UNKNOWN, "SessionAttributes에 view가 설정되지 않았습니다.");
        }
    }
}
