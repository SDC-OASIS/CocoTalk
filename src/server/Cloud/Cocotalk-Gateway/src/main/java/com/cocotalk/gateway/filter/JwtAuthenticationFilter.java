package com.cocotalk.gateway.filter;

import com.cocotalk.gateway.dto.TokenPayload;
import com.cocotalk.gateway.exception.CustomError;
import com.cocotalk.gateway.exception.CustomException;
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
            String requestPath = request.getPath().toString();
            log.info("API Gateway Request Path = [{}]", requestPath);

            List<String> accessList = request.getHeaders().get("X-ACCESS-TOKEN"); // (1) X-ACCESS-TOKEN 추출

            if(accessList == null) {
                List<String> refreshList = request.getHeaders().get("X-REFRESH-TOKEN"); // (2) X-REFRESH-TOKEN 추출
                if(refreshList == null) {
                    throw new CustomException(CustomError.JWT_AUTHENTICATION, "헤더에 토큰이 설정되지 않았습니다.");
                }
                String refreshToken = refreshList.get(0);
                if(requestPath.contains("reissue")) {
                    return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        log.info("need to be reissued X-REFRESH-TOKEN : {}", refreshToken);
                    }));
                } else {
                    TokenPayload payload = JwtUtil.getPayload(refreshToken, objectMapper); // (2) X-REFRESH-TOKEN Authenticaiton
                    return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                        log.info("userId inside X-REFRESH-TOKEN : {}", payload.getUserId());
                        log.info("fcmToken inside X-REFRESH-TOKEN : {}", payload.getFcmToken());
                    }));
                }
            } else {
                String accessToken = accessList.get(0);
                TokenPayload payload = JwtUtil.getPayload(accessToken, objectMapper); // (2) JWT Authenticaiton

                return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    log.info("userId inside X-ACCESS-TOKEN : {}", payload.getUserId());
                    log.info("fcmToken inside X-ACCESS-TOKEN : {}", payload.getFcmToken());
                }));
            }
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
