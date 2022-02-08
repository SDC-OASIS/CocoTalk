package com.cocotalk.chat.websocket.interceptor;

import com.cocotalk.chat.service.RoomService;
import com.cocotalk.chat.utils.logging.ChannelLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class InboundChannelInterceptor implements ChannelInterceptor {
    private final RoomService roomService;
    private final ChannelLogger channelLogger;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command.equals(StompCommand.CONNECT)) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String view = accessor.getFirstNativeHeader("view");
            if (sessionAttributes != null && view != null) {
                sessionAttributes.put("view", view);
                if (view.equals("chatRoom")) { // 두 세션 ID 다르다는 것을 전제로
                    if(accessor.getFirstNativeHeader("roomId") != null &&
                            accessor.getFirstNativeHeader("userId") != null){
                        ObjectId roomId = new ObjectId(accessor.getFirstNativeHeader("roomId"));
                        Long userId = Long.parseLong(accessor.getFirstNativeHeader("userId"));
                        sessionAttributes.put("roomId", roomId);
                        sessionAttributes.put("userId", userId);
                        roomService.saveEnteredAt(roomId, userId);
                    }
                } else if (view.equals("chatList")) {
                    if(accessor.getFirstNativeHeader("userId") != null) {
                        Long userId = Long.parseLong(accessor.getFirstNativeHeader("userId"));
                        sessionAttributes.put("userId", userId);
                    }
                }
            }
        } else if (command.equals(StompCommand.DISCONNECT)) {
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
                            if (sessionAttributes.get("action").equals("leave"))
                                roomService.saveLeftAt(roomId, userId);
                        } else {
                            roomService.saveAwayAt(roomId, userId); // leave action이 없으면 away
                        }
                    }
                } else if (view.equals("chatList")) {
                }
            }
        }
        channelLogger.loggingMessage(message);
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        channelLogger.loggingMessage(message);
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        channelLogger.loggingMessage(message);
        log.info(String.format("<----------------------------------- %s ----------------------------------->",
                Thread.currentThread().getStackTrace()[1].getMethodName()));
    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        log.info(channelLogger.getPath());
        return ChannelInterceptor.super.preReceive(channel);
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        channelLogger.loggingMessage(message);
        return message;
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        channelLogger.loggingMessage(message);
        log.info(String.format("<----------------------------------- %s ----------------------------------->",
                Thread.currentThread().getStackTrace()[1].getMethodName()));
    }
}
