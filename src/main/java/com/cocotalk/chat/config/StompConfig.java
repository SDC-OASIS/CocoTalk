package com.cocotalk.chat.config;

import com.cocotalk.chat.websocket.interceptor.InboundChannelInterceptor;
import com.cocotalk.chat.websocket.interceptor.OutboundChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {
    // private final AuthenticationChannelInterceptor authenticationChannelInterceptor;
    private final InboundChannelInterceptor inboundChannelInterceptor;
    private final OutboundChannelInterceptor outboundChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
                // .setHeartbeatTime(1000);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // STOMP 메시지의 "destination' 헤더는 @Controller 객체의 @MessageMapping 메서드로 라우팅 된다.
        registry.setPreservePublishOrder(true) // clientOutboundChannel에 pub된 순서를 보장
                .setApplicationDestinationPrefixes("/simple") // 작성된 메시지는 이곳으로 보내짐
                .enableSimpleBroker("/topic", "/queue"); // 이곳을 subscribe하고 있으면 메시지를 맞을 수 있음
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(inboundChannelInterceptor);
        // registration.interceptors(authenticationChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(outboundChannelInterceptor);
    }

//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//        // 시간과 보내는 버퍼 사이즈 제한
//        registration.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
//        //전송받고 보낼 메시지 사이즈 제한
//        registration.setMessageSizeLimit(128 * 1024);
//    }
}