package com.cocotalk.auth.application;

import com.cocotalk.auth.dto.common.ClientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("ClientArgumentResolver");
//        log.info("has @ClientIp : "+parameter.hasParameterAnnotation(ClientType.class));
        log.info("has ClientIp : "+parameter.getParameterType().equals(ClientType.class));

//        return parameter.hasParameterAnnotation(ClientType.class);
        return parameter.getParameterType().equals(ClientType.class);

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
        return parseClientType(agent);
    }

    private ClientType parseClientType(String userAgent){
        if(userAgent.contains("Mozilla"))
            return ClientType.MOBILE;
        return ClientType.WEB;
    }
}