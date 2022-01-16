package com.cocotalk.chat.application;

import com.cocotalk.chat.service.MessageService;
import com.cocotalk.chat.utils.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboundChannelInterceptor implements ChannelInterceptor {
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        StompHeaderAccessor afterAccessor = StompHeaderAccessor.wrap(message);
        if(StompCommand.SEND.equals(afterAccessor.getCommand())){
            log.info("StompCommand : " + StompCommand.SEND);
        } else if(StompCommand.ACK.equals((afterAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.ACK);
        } else if(StompCommand.COMMIT.equals((afterAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.COMMIT);
        }
    }
}
