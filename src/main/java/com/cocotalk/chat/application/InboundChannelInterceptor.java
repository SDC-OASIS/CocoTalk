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
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class InboundChannelInterceptor implements ChannelInterceptor {
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor preAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.BEGIN.equals((preAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.BEGIN);
        } else if(StompCommand.ACK.equals((preAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.ACK);
        } else if(StompCommand.COMMIT.equals((preAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.COMMIT);
        } else if(StompCommand.SEND.equals((preAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.SEND);
        } else if(StompCommand.CONNECT.equals((preAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.CONNECT);
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor postAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.SEND.equals((postAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.SEND);
        } else if(StompCommand.ACK.equals((postAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.ACK);
        } else if(StompCommand.COMMIT.equals((postAccessor.getCommand()))){
            log.info("StompCommand : " + StompCommand.COMMIT);
        }
    }
}
