package com.cocotalk.presence.utils;

import com.cocotalk.presence.application.TokenPayload;
import com.cocotalk.presence.exception.CustomError;
import com.cocotalk.presence.exception.CustomException;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;

@Slf4j
@Component
public class JwtUtil {
    private static String jwtSecret;

    @Value("${jwt.secret}")
    public void setJwtSecret(String secret) {
        jwtSecret = secret;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static TokenPayload getPayload(String accessToken) { // JWT Parsing
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        try {
            TokenPayload payload = objectMapper.readValue(claims.getSubject(), TokenPayload.class);
            return payload;
        } catch (JacksonException e) {
            e.printStackTrace();
            log.error("[JwtUtil/getPayload] : Jwt Payload를 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }
}
