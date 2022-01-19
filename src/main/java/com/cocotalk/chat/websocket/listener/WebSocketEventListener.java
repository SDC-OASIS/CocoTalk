package com.cocotalk.chat.websocket.listener;

import com.cocotalk.chat.document.message.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleBrokerAvailabilityListener(BrokerAvailabilityEvent event) {
        log.info("BrokerAvailabilityEvent - timestamp: " + event.getTimestamp());
        log.info("BrokerAvailabilityEvent - brokerAvailable: " + event.isBrokerAvailable());
        log.info("BrokerAvailabilityEvent - " + event.toString());
    }

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
        log.info("SessionConnectedEvent - timestamp: " + event.getTimestamp());
        log.info("SessionConnectedEvent - user: " + event.getUser());
        log.info("SessionConnectedEvent: " + event.toString());

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        StompCommand command = accessor.getCommand();
//
//        log.info("SessionConnectedEvent : StompCommand: " + command);
        log.info("SessionConnectedEvent - login: " + accessor.getLogin());

        long[] heartBeats = accessor.getHeartbeat();
        for (long heartBeat : heartBeats) {
            log.info("SessionConnectedEvent - heartBeat: " + heartBeat);
        }

        log.info("SessionConnectedEvent - destination: " + accessor.getDestination());
        log.info("SessionConnectedEvent - host: " + accessor.getHost());
        log.info("SessionConnectedEvent - message: " + accessor.getMessage());
        log.info("SessionConnectedEvent - sessionId: " + accessor.getSessionId());
        log.info("SessionConnectedEvent - subscriptionId: " + accessor.getSubscriptionId());

        byte[] payload = (byte[])event.getMessage().getPayload();
        String stringPayload = new String(payload);
        log.info("SessionConnectedEvent - payload: " + stringPayload);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("SessionDisconnectEvent - timestamp: " + event.getTimestamp());
        log.info("SessionDisconnectEvent - user: " + event.getUser());
        log.info("SessionDisconnectEvent - sessionId: " + event.getSessionId());
        log.info("SessionDisconnectEvent - close status: " + event.getCloseStatus());
        log.info("SessionDisconnectEvent: " + event.toString());

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        StompCommand command = accessor.getCommand();
//
//        log.info("SessionDisconnectEvent : StompCommand: " + command);
        log.info("SessionDisconnectEvent - login: " + accessor.getLogin());

        long[] heartBeats = accessor.getHeartbeat();
        for (long heartBeat : heartBeats) {
            log.info("SessionDisconnectEvent - heartBeat: " + heartBeat);
        }

        log.info("SessionDisconnectEvent - destination: " + accessor.getDestination());
        log.info("SessionDisconnectEvent - host: " + accessor.getHost());
        log.info("SessionDisconnectEvent - message: " + accessor.getMessage());
        log.info("SessionDisconnectEvent - sessionId: " + accessor.getSessionId());
        log.info("SessionDisconnectEvent - subscriptionId: " + accessor.getSubscriptionId());

        byte[] payload = (byte[])event.getMessage().getPayload();
        String stringPayload = new String(payload);
        log.info("SessionDisconnectEvent - payload: " + stringPayload);

        Long userId = (Long) accessor.getSessionAttributes().get("userId");
        if(userId != null) {
            log.info("User Disconnected - userId = " + userId);

            ChatMessage chatMessage = ChatMessage.builder()
                    .type(2)
                    .userId(userId)
                    .build();

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
