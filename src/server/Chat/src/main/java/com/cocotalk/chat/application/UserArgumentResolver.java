package com.cocotalk.chat.application;

import com.cocotalk.chat.domain.vo.UserVo;
import com.cocotalk.chat.dto.TokenPayload;
import com.cocotalk.chat.dto.response.CustomResponse;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.cocotalk.chat.utils.JwtUtil;
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

    public static final CustomException INVALID_USERID =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 유저가 존재하지 않습니다.");

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
        if (!StringUtils.hasLength(token)) throw new CustomException(CustomError.NOT_LOGIN);

        TokenPayload payload = JwtUtil.getPayload(token);

        HttpHeaders headers = new HttpHeaders();
        headers.set(TOKEN_HEADER_NAME, token);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl(USER_SERVICE_URL);

        ResponseEntity<CustomResponse> response = restTemplate.exchange(
                URI.create(uriComponentsBuilder.build().toUriString()),
                HttpMethod.GET,
                request,
                CustomResponse.class);

        Object data = response.getBody().getData();

        if(data == null) throw INVALID_USERID;
        
        return objectMapper.convertValue(data, UserVo.class);
    }
}