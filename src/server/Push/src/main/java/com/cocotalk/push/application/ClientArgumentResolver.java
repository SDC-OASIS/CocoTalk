package com.cocotalk.push.application;

import com.cocotalk.push.dto.common.ClientType;
import com.cocotalk.push.dto.common.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * 요청 해더를 통해 user-agent, client ip 정보를 찾아 controlloer에서 필요한 ClientInfo.class 형태로 가공합니다.
 */
@Slf4j
public class ClientArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ClientInfo.class);
    }

    @Override
    public Mono<Object> resolveArgument(
            MethodParameter parameter,
            BindingContext bindingContext,
            ServerWebExchange exchange
    ) {
        // userAgent와 요청 ip 정보로 ClientInfo 객체를 생성하여 반환합니다.
        String agent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        String clientIp;
        String xForwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.isEmpty(xForwarded) || "unknown".equalsIgnoreCase(xForwarded)) {
            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            if(remoteAddress==null)
                return Mono.empty();
            clientIp = remoteAddress.getAddress().getHostAddress();
        } else{
            clientIp = xForwarded.split(",")[0];
        }
        ClientType clientType = parseClientType(agent);
        return Mono.just(ClientInfo.builder()
                .ip(clientIp)
                .agent(agent)
                .clientType(clientType)
                .build());
    }

    /**
     * userAgent를 파싱하여 ClientType 정보로 반합니다.
     *
     * @param userAgent 요청자의 userAgent
     * @return userAgent에 해당하는 ClientType
     */
    private ClientType parseClientType(String userAgent){
        if(userAgent.contains("Mozilla"))
            return ClientType.WEB;
        return ClientType.MOBILE;
    }
}