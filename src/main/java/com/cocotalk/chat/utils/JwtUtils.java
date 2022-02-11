package com.cocotalk.chat.utils;

import com.cocotalk.chat.dto.TokenPayload;
import com.cocotalk.chat.exception.CustomError;
import com.cocotalk.chat.exception.CustomException;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

@Slf4j
@Component
public class JwtUtils {
    private static String jwtSecret;

    @Value("${jwt.secret}")
    public void setJwtSecret(String secret) {
        jwtSecret = secret;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    public static String getRefreshToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-REFRESH-TOKEN");
    }

    public static TokenPayload getPayload() {
        String accessToken = getAccessToken();
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
            try {
                TokenPayload payload = objectMapper.readValue(claims.getSubject(), TokenPayload.class);
                return payload;
            } catch (JacksonException e) {
                throw new CustomException(CustomError.JSON_PARSE, e);
            }
        } catch (Throwable ex) {
            log.warn("getPayload : " + ex);
            String message = "";
            if (ex.getClass() == NullPointerException.class) {
                message = "X-ACCESS-TOKEN 헤더가 설정되지 않았습니다.";
            } else if (ex.getClass() == ExpiredJwtException.class) {
                message = "만료된 토큰입니다.";
            } else if (ex.getClass() == MalformedJwtException.class ||
                    ex.getClass() == SignatureException.class ||
                    ex.getClass() == UnsupportedJwtException.class) {
                message = "올바르지 않은 형식의 토큰입니다.";
            } else if (ex.getClass() == IllegalArgumentException.class) {
                message = "헤더에 토큰이 포함되지 않았습니다.";
            }
            throw new CustomException(CustomError.JWT_AUTHENTICATION, message);
        }
    }
}