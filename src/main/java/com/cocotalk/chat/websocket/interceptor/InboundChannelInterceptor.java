package com.cocotalk.chat.websocket.interceptor;

import com.cocotalk.chat.service.RoomService;
import com.cocotalk.chat.utils.logging.ChannelLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
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
            List<String> viewList = accessor.getNativeHeader("view");
            if (sessionAttributes != null && viewList != null) {
                String view = viewList.get(0);
                sessionAttributes.put("view", view);
                if(view.equals("chatroom")) {
                    List<String> uiList = accessor.getNativeHeader("userId");
                    List<String> riList = accessor.getNativeHeader("roomId");
                    if(uiList != null && riList != null) {
                        sessionAttributes.put("userId", uiList.get(0));
                        sessionAttributes.put("roomId", riList.get(0));
                    }
                } else if (view.equals("main")) {
                    // 메인화면 글로벌 소켓 예정
                }
            }
        }  else if(command.equals(StompCommand.DISCONNECT)) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if(sessionAttributes != null) {
                String view = sessionAttributes.get("view").toString();
                if(view.equals("chatroom")) {
                    Long userId = Long.parseLong(sessionAttributes.get("userId").toString());
                    ObjectId roomId = new ObjectId(sessionAttributes.get("roomId").toString());
                    roomService.saveLeftAt(roomId.toHexString(), userId);
                } else if (view.equals("main")) {
                    // 메인화면 글로벌 소켓 예정
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
