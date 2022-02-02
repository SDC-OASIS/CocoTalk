package com.cocotalk.chat.websocket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
//    private final SimpMessageSendingOperations messagingTemplate;
//
//    @EventListener
//    public void handleBrokerAvailabilityListener(BrokerAvailabilityEvent event) {
//        log.info("BrokerAvailabilityEvent - timestamp: " + event.getTimestamp());
//        log.info("BrokerAvailabilityEvent - brokerAvailable: " + event.isBrokerAvailable());
//        log.info("BrokerAvailabilityEvent - " + event.toString());
//    }
//
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
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
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
//        if(sessionAttributes != null) {
//            String view = sessionAttributes.get("view").toString();
//            if(view != null) {
//                if(view.equals("room")) {
//                    Long userId = Long.parseLong(sessionAttributes.get("userId").toString());
//                    ObjectId roomId = new ObjectId(sessionAttributes.get("roomId").toString());
//                    log.info("User Disconnected - userId = " + userId);
//                } else if (view.equals("roomList")) {
//                    // 메인화면 글로벌 소켓 예정
//                }
//            }
//        }
//    }
}
