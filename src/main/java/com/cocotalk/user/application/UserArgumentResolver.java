//package com.cocotalk.user.application;
//
//import com.cocotalk.user.domain.entity.User;
//import com.cocotalk.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Component
//@RequiredArgsConstructor
//public class UserArgumentResolver implements HandlerMethodArgumentResolver {
//    private final UserRepository userRepository;
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return parameter.getParameterType().equals(User.class);
//    }
//
//    @Override
//    public Object resolveArgument(
//            MethodParameter parameter,
//            ModelAndViewContainer mavContainer,
//            NativeWebRequest webRequest,
//            WebDataBinderFactory binderFactory
//    ){
//        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
//        String token = req.getHeader("x-access-token");
//        if (!StringUtils.hasLength(token)) throw new GlobalException(GlobalError.NOT_LOGIN);
//        TokenPayload payload = ca.decrypt(token);
//        User user = userRepository.findById(payload.getId()).orElseThrow(() -> new GlobalException(GlobalError.BAD_REQUEST, "Token으로 유저를 찾을 수 없습니다."));
//        return user;
//    }
//}
