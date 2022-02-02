package com.cocotalk.push;

import com.cocotalk.push.dto.device.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Slf4j
public class ClientIpArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ClientInfo.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        String agent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        System.out.println(agent);
//        for (Map.Entry<String, List<String>> entry : exchange.getRequest().getHeaders().entrySet()) {
//            System.out.println("[key]:" + entry.getKey() + ", [value]:" + entry.getValue());
//        }
        if (remoteAddress != null) {
            String address = remoteAddress.getAddress().getHostAddress();
            return Mono.just(ClientInfo.builder().ip(address).agent(agent).build());
        }
        return Mono.empty();
    }
}