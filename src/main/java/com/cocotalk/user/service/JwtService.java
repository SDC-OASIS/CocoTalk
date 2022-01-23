package com.cocotalk.user.service;

import com.cocotalk.user.domain.entity.User;
import com.cocotalk.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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

    public String getUserCid() {
        String accessToken = getAccessToken();
        if (accessToken == null)
            return null;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
            String userCid = claims.getBody().get("userCid", String.class);
            if (StringUtils.isEmpty(userCid))
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
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
            String userCid = claims.getBody().get("userCid", String.class);
            if (StringUtils.isEmpty(userCid))
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
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
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
}