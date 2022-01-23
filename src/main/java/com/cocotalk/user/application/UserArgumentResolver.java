package com.cocotalk.user.application;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.dto.exception.GlobalError;
import com.cocotalk.user.dto.exception.GlobalException;
import com.cocotalk.user.repository.UserRepository;
import com.cocotalk.user.service.JwtService;
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
    private final JwtService jwtService;

    public static final GlobalException INVALID_CID = new GlobalException(GlobalError.BAD_REQUEST, "해당 코코톡 id를 갖는 유저가 존재하지 않습니다.");

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
        String token = req.getHeader("X-ACCESS-TOKEN");
        if (!StringUtils.hasLength(token)) throw new GlobalException(GlobalError.NOT_LOGIN);
        String userCid = jwtService.getUserCid();
        return userRepository.findByCid(userCid).orElseThrow(() -> INVALID_CID);
    }
}
