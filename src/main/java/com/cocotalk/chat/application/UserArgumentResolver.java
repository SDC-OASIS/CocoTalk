package com.cocotalk.chat.application;

import com.cocotalk.chat.domain.vo.UserVo;
import com.cocotalk.chat.dto.TokenPayload;
import com.cocotalk.chat.dto.response.GlobalResponse;
import com.cocotalk.chat.exception.GlobalError;
import com.cocotalk.chat.exception.GlobalException;
import com.cocotalk.chat.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Slf4j
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Value(value = "${oci.user.url}")
    private String USER_SERVICE_URL;
    private static final String TOKEN_HEADER_NAME = "X-ACCESS-TOKEN";

    public static final GlobalException INVALID_USERID =
            new GlobalException(GlobalError.BAD_REQUEST, "해당 userId를 갖는 유저가 존재하지 않습니다.");

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserVo.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ){
        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
        String token = req.getHeader(TOKEN_HEADER_NAME);
        if (!StringUtils.hasLength(token)) throw new GlobalException(GlobalError.NOT_LOGIN);

        HttpHeaders headers = new HttpHeaders();
        headers.set(TOKEN_HEADER_NAME, token);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        TokenPayload payload = JwtUtils.getPayload();

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(USER_SERVICE_URL).path(payload.getUserId().toString());

        ResponseEntity<GlobalResponse> response = restTemplate.exchange(
                URI.create(uriComponentsBuilder.build().toUriString()),
                HttpMethod.GET,
                request,
                GlobalResponse.class);

        Object data = response.getBody().getData();

        if(data == null) throw INVALID_USERID;
        
        return objectMapper.convertValue(data, UserVo.class);
    }
}