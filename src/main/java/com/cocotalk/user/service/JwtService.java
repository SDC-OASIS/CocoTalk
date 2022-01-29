package com.cocotalk.user.service;

import com.cocotalk.user.dto.TokenPayload;
import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.cocotalk.user.repository.UserRepository;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    public String getRefreshToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-REFRESH-TOKEN");
    }

    public TokenPayload getPayload() {
        String accessToken = getAccessToken();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        try {
            TokenPayload payload = objectMapper.readValue(claims.getSubject(), TokenPayload.class);
            return payload;
        } catch (JacksonException e) {
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }
}