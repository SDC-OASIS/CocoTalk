package com.cocotalk.chat.application;

import com.cocotalk.chat.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@NoArgsConstructor
public class AuthenticationChannelInterceptor implements ChannelInterceptor {
    private int num = 1;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            User user = User.builder()
                    .name(accessor.getLogin())
                    .build();
            accessor.setUser(user);
        }
        return message;
    }
}
