package com.cocotalk.gateway.filter;

import com.cocotalk.gateway.dto.TokenPayload;
import com.cocotalk.gateway.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Bean
    public ErrorWebExceptionHandler jwtWebExceptionHandler() {
        return new JwtUtil.JwtWebExceptionHandler();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            List<String> list = request.getHeaders().get("X-ACCESS-TOKEN"); // (1) X-ACCESS-TOKEN 추출
            String token = Objects.requireNonNull(list).get(0);
            TokenPayload payload = JwtUtil.getPayload(token, objectMapper); // (2) JWT Authenticaiton

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        log.info("userId inside X-ACCESS-TOKEN : {}", payload.getUserId());
                        log.info("fcmToken inside X-ACCESS-TOKEN : {}", payload.getFcmToken());
                    }));
        };
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        private Long userId;
        private String fcmToken;
    }
}
