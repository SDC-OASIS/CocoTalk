package com.cocotalk.auth.utils;

import com.cocotalk.auth.exception.CustomException;
import com.cocotalk.auth.dto.common.payload.TokenPayload;
import com.cocotalk.auth.dto.common.response.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtUtils {
    private static String jwtSecret;
    private static long accessTokenExp;
    private static long refreshTokenExp;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret}")
    public void setJwtSecret(String secret) {
        jwtSecret = secret;
    }

    @Value("${jwt.token.exp.access}")
    public void setAccessTokenExp(long exp) {
        accessTokenExp = exp;
    }

    @Value("${jwt.token.exp.refresh}")
    public void setRefreshTokenExp(long exp) {
        refreshTokenExp = exp;
    }

    public static String createAccessToken(long userId, String fcmToken) {
        log.info("jwtSecret:"+jwtSecret);
        TokenPayload tokenPayload = TokenPayload.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .build();
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());
        Date now = new Date();
        String token = null;
        try {
            token = Jwts.builder()
                    .setSubject(objectMapper.writeValueAsString(tokenPayload))
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + accessTokenExp * 1000))
//                    .setExpiration(new Date(now.getTime() + 30 * 1000))
                    .signWith(signingKey, signatureAlgorithm)
                    .compact();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static String createRefreshToken(long userId, String fcmToken) {
        TokenPayload tokenPayload = TokenPayload.builder()
                .userId(userId)
                .fcmToken(fcmToken)
                .build();
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());
        Date now = new Date();
        String token = null;
        try {
            token = Jwts.builder()
                    .setSubject(objectMapper.writeValueAsString(tokenPayload))
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + refreshTokenExp * 1000))
                    .signWith(signingKey, signatureAlgorithm)
                    .compact();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static String getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    public static String getRefreshToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-REFRESH-TOKEN");
    }

    public static TokenPayload getPayload(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        try {
            TokenPayload payload = objectMapper.readValue(claims.getSubject(), TokenPayload.class);
            return payload;
        } catch (JacksonException e) {
            throw new CustomException(ResponseCode.UNAUTHORIZED, e);
        }
    }

}
