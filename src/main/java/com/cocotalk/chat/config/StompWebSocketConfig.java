package com.cocotalk.chat.config;

import com.cocotalk.chat.application.InboundChannelInterceptor;
import com.cocotalk.chat.application.OutboundChannelInterceptor;
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
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // private final AuthenticationChannelInterceptor authenticationChannelInterceptor;
    private final InboundChannelInterceptor inboundChannelInterceptor;
    private final OutboundChannelInterceptor outboundChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /example은 WebSocket 또는 SockJS Client가 웹 소켓 핸드셰이크 커넥션을 생성할 경로이다.

        // endpoint를 /stomp로 하고, AllowedOrigins를 "*"로 하면
        // 페이지에서 GET /info 404 에러가 발생한다. 그래서 아래와 같이 2개의 계층으로
        // 분리하고 origins를 개발 도메인으로 변경하니 잘 동작하였다.
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /test 경로로 시작하는 STOMP 메시지의 "destination' 헤더는 @Controller 객체의 @MessageMapping 메서드로 라우팅 된다.
        registry.setPreservePublishOrder(true) // clientOutboundChannel에 pub된 순서를 보장
                .setApplicationDestinationPrefixes("/simple")
                .enableSimpleBroker("/topic", "/queue");
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