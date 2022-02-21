package com.cocotalk.chat.utils.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChannelLogger {
    public String getPath() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        return className + " / " + methodName + " : ";
    }

    public void loggingMessage(Message<?> message) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        final StompCommand command = headerAccessor.getCommand() != null ? headerAccessor.getCommand() : StompCommand.ACK;
        String data = message.getPayload() + getPath();
        if(StompCommand.STOMP.equals((command))){
            log.info(data + StompCommand.STOMP);
        } else if(StompCommand.BEGIN.equals((command))){
            log.info(data + StompCommand.BEGIN);
        } else if(StompCommand.ACK.equals((command))){
            log.info(data + StompCommand.ACK);
        } else if(StompCommand.NACK.equals((command))){
            log.info(data + StompCommand.NACK);
        } else if(StompCommand.ABORT.equals((command))){
            log.info(data + StompCommand.ABORT);
        } else if(StompCommand.COMMIT.equals((command))){
            log.info(data + StompCommand.COMMIT);
        }  else if(StompCommand.CONNECT.equals((command))){
            log.info(data + StompCommand.CONNECT);
        } else if(StompCommand.SUBSCRIBE.equals((command))){
            log.info(data + StompCommand.SUBSCRIBE);
        } else if(StompCommand.UNSUBSCRIBE.equals((command))){
            log.info(data + StompCommand.UNSUBSCRIBE);
        } else if(StompCommand.SEND.equals((command))){
            log.info(data + StompCommand.SEND);
        } else if(StompCommand.ERROR.equals((command))){
            log.info(data  + StompCommand.ERROR);
        } if(StompCommand.RECEIPT.equals((command))){
            log.info(data + StompCommand.RECEIPT);
        }
    }
}
