package com.cocotalk.gateway.utils;

import com.cocotalk.gateway.dto.ErrorResponse;
import com.cocotalk.gateway.exception.CustomError;
import com.cocotalk.gateway.exception.CustomException;
import com.cocotalk.gateway.exception.ErrorDetails;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {
    private static String jwtSecret;

    @Value("${jwt.secret}")
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public static Map<String, Object> getPayload(String token, ObjectMapper objectMapper) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        try {
            Map<String, Object> payload = objectMapper.readValue(claims.getSubject(), Map.class);
            return payload;
        } catch (JacksonException e) {
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

    public static class JwtWebExceptionHandler implements ErrorWebExceptionHandler {
        private String serializeError(CustomError error, String message) {
            CustomException exception = new CustomException(error, message);
            ErrorDetails details = new ErrorDetails(exception);

            log.error("CustomException : " + exception.getMessage());

            ErrorResponse response = new ErrorResponse(details);
            try {
                return ObjectMapperUtils.serialization(response);
            } catch (JsonProcessingException e) {
                throw new CustomException(CustomError.JSON_PARSE, e);
            }
        }

        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
            log.warn("JwtWebExceptionHandler : " + ex);

            String message = "";
            if (ex.getClass() == NullPointerException.class) {
                message = "X-ACCESS-TOKEN 헤더가 설정되지 않았습니다.";
            }
            else if (ex.getClass() == ExpiredJwtException.class) {
                message = "만료된 토큰입니다.";
            } else if (ex.getClass() == MalformedJwtException.class ||
                    ex.getClass() == SignatureException.class ||
                    ex.getClass() == UnsupportedJwtException.class) {
                message = "올바르지 않은 형식의 토큰입니다.";
            } else if (ex.getClass() == IllegalArgumentException.class) {
                message = "헤더에 토큰이 포함되지 않았습니다.";
            }

            log.error(ex.getMessage());
            log.error(ex.getCause().getMessage());
            ex.printStackTrace();

            byte[] bytes = serializeError(CustomError.JWT_AUTHENTICATION, message).getBytes(StandardCharsets.UTF_8);
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }
}
