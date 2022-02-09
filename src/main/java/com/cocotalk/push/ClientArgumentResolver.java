package com.cocotalk.push;

import com.cocotalk.push.dto.common.ClientType;
import com.cocotalk.push.dto.device.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

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
        log.info("[resolveArgument/agent] : " + agent);
        log.info("[resolveArgument/clientIp] : " + clientIp);
        return Mono.just(ClientInfo.builder()
                .ip(clientIp)
                .agent(agent)
                .clientType(clientType)
                .build());
    }

    private ClientType parseClientType(String userAgent){
        if(userAgent.contains("Mozilla"))
            return ClientType.WEB;
        return ClientType.MOBILE;
    }
}