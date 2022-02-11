package com.cocotalk.user.application;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.domain.vo.TokenPayload;
import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    public static final CustomException INVALID_USERID =
            new CustomException(CustomError.BAD_REQUEST, "해당 userId를 갖는 유저가 존재하지 않습니다.");

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ){
        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
        String token = req.getHeader("X-ACCESS-TOKEN"); // (1) X-ACCESS-TOKEN 추출
        if (!StringUtils.hasLength(token)) throw new CustomException(CustomError.NOT_LOGIN);
        TokenPayload payload = JwtUtils.getPayload();
        Long userId = payload.getUserId();
        return userRepository.findById(userId).orElseThrow(() -> INVALID_USERID); // (2) JWT 인가
    }
}
