package com.cocotalk.chat.websocket.listener;

import com.cocotalk.chat.domain.vo.RoomVo;
import com.cocotalk.chat.dto.request.PresenceRequest;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.service.RoomService;
import com.cocotalk.chat.service.kafka.KafkaProducer;
import com.cocotalk.chat.utils.WebSocketUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.DependsOn;
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
@DependsOn("ServerUrl")
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final RoomService roomService;
    private final ObjectMapper objectMapper;
    private final WebSocketUtil webSocketUtil;
    private final KafkaProducer kafkaProducer;

    @EventListener
    public void handleBrokerAvailabilityListener(BrokerAvailabilityEvent event) {
        log.info("BrokerAvailabilityEvent - timestamp: " + event.getTimestamp());
        log.info("BrokerAvailabilityEvent - brokerAvailable: " + event.isBrokerAvailable());
        log.info("BrokerAvailabilityEvent - " + event.toString());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) { // WebSocket 연결 이벤트 발생시
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        handleWebSocketConnectSession(accessor); // 세션 관리 + 상태 정보 업데이트
        PresenceRequest connectRequest = PresenceRequest.builder()
                .action("connect") // 채팅 관리 서버에 메시지 전송 -> redis에 stompEndpoint를 key로 갖는 value값 + 1 (connection 수)
                .serverUrl(webSocketUtil.getStompEndpoint())
                .build();
        try {
            webSocketUtil.send(objectMapper.writeValueAsString(connectRequest));
            log.info("sessionId = {}, {}에 연결됨.", accessor.getSessionId(), webSocketUtil.getStompEndpoint());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("[WebSocketEventListener/handleWebSocketConnectListener] : connect 요청을 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

    public void handleWebSocketConnectSession(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String view = accessor.getFirstNativeHeader("view");
        if (view != null && sessionAttributes != null) {
            sessionAttributes.put("view", view);
            if (view.equals("chatRoom")) { // 채팅방 소켓일때
                try {
                    ObjectId roomId = new ObjectId(Objects.requireNonNull(accessor.getFirstNativeHeader("roomId")));
                    Long userId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("userId")));
                    sessionAttributes.put("roomId", roomId);
                    sessionAttributes.put("userId", userId);

                    RoomVo roomVo = roomService.saveEnteredAt(roomId, userId); // 유저가 채팅방 소켓 연결 된 시각 업데이트
                    kafkaProducer.sendToChat("/topic/" + roomVo.getId() + "/room", roomVo); // 업데이트 된 채팅방 정보 전송
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    log.error("[WebSocketEventListener/handleWebSocketConnectSession] : ChatRoom Connect 헤더에 roomId 또는 userId가 설정되지 않았습니다.");
                    throw new CustomException(CustomError.BAD_REQUEST, e);
                }
            } else if (view.equals("chatList")) { // 채팅방 리스트 소켓일때
                try {
                    Long userId = Long.parseLong(Objects.requireNonNull(accessor.getFirstNativeHeader("userId")));
                    sessionAttributes.put("userId", userId);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    log.error("[WebSocketEventListener/handleWebSocketConnectSession] : ChatList Connect 헤더에 userId가 설정되지 않았습니다.");
                    throw new CustomException(CustomError.BAD_REQUEST, e);
                }
            }
        } else {
            log.error("[WebSocketEventListener/handleWebSocketConnectSession] : Connect 헤더에 view가 설정되지 않았습니다.");
            throw new CustomException(CustomError.BAD_REQUEST, "Connect 헤더에 view가 설정되지 않았습니다.");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) { // WebSocket 연결 해제 이벤트 발생시
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        handleWebSocketDisconnectSession(accessor); // 세션 관리 + 상태 정보 업데이트
        PresenceRequest disconnectRequest = PresenceRequest.builder()
                .action("disconnect") // 채팅 관리 서버에 메시지 전송 -> redis에 stompEndpoint를 key로 갖는 value값 + 1 (connection 수)
                .serverUrl(webSocketUtil.getStompEndpoint())
                .build();
        try {
            webSocketUtil.send(objectMapper.writeValueAsString(disconnectRequest));
            log.info("sessionId = {}, {}과 연결 끊어짐.", accessor.getSessionId(), webSocketUtil.getStompEndpoint());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("[WebSocketEventListener/handleWebSocketConnectListener] : disconnect 요청을 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

    public void handleWebSocketDisconnectSession(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.get("view") != null) {
            String view = sessionAttributes.get("view").toString();
            if (view.equals("chatRoom")) { // 채팅방 소켓일때
                String action = accessor.getFirstNativeHeader("action");
                if (action != null) { // 채팅방에서 나가기 버튼 눌렀을 때
                    sessionAttributes.put("action", action);
                } else { // 단순 Disconnect일 때
                    Long userId = (Long) sessionAttributes.get("userId");
                    ObjectId roomId = (ObjectId) sessionAttributes.get("roomId");
                    if (sessionAttributes.get("action") != null) {
                        if (sessionAttributes.get("action").equals("leave")) {  // 채팅방에서 나가기 버튼 눌렀을 때
                            RoomVo roomVo = roomService.saveLeftAt(roomId, userId); // 유저가 톡방에서 떠난 시각 업데이트
                            kafkaProducer.sendToChat("/topic/" + roomId + "/room", roomVo); // 업데이트 된 채팅방 정보 전송
                            int size = roomVo.getMembers().size();
                            for(int i = 0; i < size; ++i) { // 채팅방 리스트 소켓에도 업데이트 된 채팅방 정보 전송
                                long memberId = roomVo.getMembers().get(i).getUserId();
                                kafkaProducer.sendToChat("/topic/" + memberId + "/room", roomVo);
                            }
                        } else {
                            throw new CustomException(CustomError.BAD_REQUEST, "지원하지 않는 형식의 action 입니다.");
                        }
                    } else {
                        RoomVo roomVo = roomService.saveAwayAt(roomId, userId); // 유저가 채팅방 소켓 연결 해제된 시각 업데이트
                        kafkaProducer.sendToChat("/topic/" + roomId + "/room", roomVo); // 업데이트 된 채팅방 정보 전송
                    }
                }
            } else if (view.equals("chatList")) { // 채팅방 리스트 소켓일때
            }
        } else {
            throw new CustomException(CustomError.UNKNOWN, "SessionAttributes에 view가 설정되지 않았습니다.");
        }
    }
}
