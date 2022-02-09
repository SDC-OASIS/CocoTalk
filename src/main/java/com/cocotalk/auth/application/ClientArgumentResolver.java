package com.cocotalk.auth.application;

import com.cocotalk.auth.dto.common.ClientInfo;
import com.cocotalk.auth.dto.common.ClientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ClientInfo.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ){
        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
        String agent = req.getHeader("user-agent");

        ClientType clientType = parseClientType(agent);
        String clientIp;
        String xForwarded = req.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(xForwarded) || "unknown".equalsIgnoreCase(xForwarded)) {
            clientIp = req.getRemoteAddr();
        } else{
            clientIp = xForwarded.split(",")[0];
            log.info("[resolveArgument/xForwarded] : " + xForwarded);
            log.info("[resolveArgument/clientIp] : " + clientIp);
        }

        return ClientInfo.builder()
                .ip(clientIp)
                .agent(agent)
                .clientType(clientType)
                .build();
    }

    private ClientType parseClientType(String userAgent){
        if(userAgent.contains("Mozilla"))
            return ClientType.WEB;
        return ClientType.MOBILE;
    }
}