package com.cocotalk.service;

import com.cocotalk.config.ValidationCheck;
import com.cocotalk.config.security.CustomUserDetailsService;
import com.cocotalk.entity.User;
import com.cocotalk.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.secret}")
    private String TOKEN_SECRET_KEY;

    @Value("${jwt.token.exp.access}")
    public long ACCESS_TOKEN_VALIDITY_SECONDS;
    @Value("${jwt.token.exp.refresh}")
    private long REFRESH_TOKEN_VALIDITY_SECONDS;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createAccessToken(String userCid) {
        Date now = new Date();
        String userId = getUserCid();
        return Jwts.builder()
                .claim("userCid", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET_KEY)
                .compact();
    }

    public String createRefreshToken(String userCid) {
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .claim("userCid", userCid)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET_KEY)
                .compact();
        User user = userRepository.findByCid(userCid).orElse(null);
        if(user==null) return null;

        return refreshToken;
    }

    public String getAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    public String getRefreshToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-REFRESH-TOKEN");
    }

    public String getUserCid() {
        String accessToken = getAccessToken();
        if (accessToken == null)
            return null;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(TOKEN_SECRET_KEY).parseClaimsJws(accessToken);
            String userCid = claims.getBody().get("userCid", String.class);
            if (!ValidationCheck.isValid(userCid))
                return null;
            User user = userRepository.findByCid(userCid).orElse(null);
            if (user == null)
                return null;
            return userCid;
        } catch (Exception exception) {
            return null;
        }
    }

    public User getUser() {
        String accessToken = getAccessToken();
        if (accessToken == null)
            return null;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(TOKEN_SECRET_KEY).parseClaimsJws(accessToken);
            String userCid = claims.getBody().get("userCid", String.class);
            if (!ValidationCheck.isValid(userCid))
                return null;
            User userDB = userRepository.findByCid(userCid).orElse(null);
            if (userDB == null)
                return null;
            return userDB;
        } catch (Exception exception) {
            return null;
        }
    }

    public Jws<Claims> getClaims(String jwtToken) {
        try {
            return Jwts.parser().setSigningKey(TOKEN_SECRET_KEY).parseClaimsJws(jwtToken);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw ex;
        }
         catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw ex;
         }
        catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }

//    public String makeToken(TokenPayload payload) {
//        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(TOKEN_SECRET_KEY);
//        Key signingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());
//        long nowMillis = System.currentTimeMillis();
//        long expMillis = nowMillis + exp;
//        Date exp = new Date(expMillis);
//        try {
//            return Jwts.builder()
//                    .setSubject(objectMapper.writeValueAsString(payload))
//                    .setIssuedAt(new Date(nowMillis))
//                    .setExpiration(exp)
//                    .signWith(signatureAlgorithm, signingKey)
//                    .compact();
//        } catch (JsonProcessingException e) {
//            throw new AuthException(AuthError.JSON_PARSE_ERROR, e);
//        }
//    }
}
