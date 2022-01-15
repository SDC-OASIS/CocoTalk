package com.cocotalk.chat.application;

import lombok.NoArgsConstructor;
import org.springframework.messaging.support.ChannelInterceptor;

@NoArgsConstructor
public class AuthenticationChannelInterceptor implements ChannelInterceptor {
    private int num = 1;
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            User user = User.builder()
//                    .name(accessor.getLogin())
//                    .build();
//            accessor.setUser(user);
//        }
//        return message;
//    }
}
