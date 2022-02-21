package com.cocotalk.auth.service;

import com.cocotalk.auth.dto.common.ClientType;
import com.cocotalk.auth.dto.common.response.ResponseStatus;
import com.cocotalk.auth.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 *
 * redis에 접근하는 서비스
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.token.exp.access}")
    long accessTokenExp;
    @Value("${jwt.token.exp.refresh}")
    long refreshTokenExp;
    @Value("${mail.exp}")
    long mailCodeExp;

    /**
     * userId와 기기타입(MOBILE or WEB)으로 RefreshToken을 조회합니다.
     *
     * @param clientType  Refresh Token을 조회할 client 기기타입 ( MOBILE or WEB)
     * @param userId Refresh Token을 조회할 userId
     * @return 조회한 Refresh Token 값
     */
    public String getRefreshToken(ClientType clientType, long userId){
        String key =  "RT/"+clientType.toString()+"/"+userId;
        log.info("[RedisService/getRefreshToken] key : "+key);
        return getData(key);
    }

    /**
     * userId와 기기타입(MOBILE or WEB)이 일치하는 Refresh Token의 정보를 생성/갱신합니다.
     *
     * @param clientType  Refresh Token을 갱신할 client 기기타입 ( MOBILE or WEB)
     * @param userId Refresh Token을 갱신할 userId
     */
    public void setRefreshToken(ClientType clientType, long userId, String refreshToken){
        String key =  "RT/"+clientType.toString()+"/"+userId;
        setDataExpire(key,refreshToken,refreshTokenExp);
    }

    /**
     * userId와 기기타입(MOBILE or WEB)이 일치하는 Refresh Token 정보를 제거합니다.
     *
     * @param clientType  Refresh Token을 제거할 client 기기타입 ( MOBILE or WEB)
     * @param userId Refresh Token을 제거할 userId
     */
    public void deleteRefreshToken(ClientType clientType, long userId){
        String key =  "RT/"+clientType.toString()+"/"+userId;
        deleteData(key);
    }

    /**
     * 해당 이메일의 인증코드를 조회합니다.
     *
     * @param email 인증코드를 조회할 eamil
     * @return 조회한 해당 이메일의 인증코드
     */
    public String getEmailCode(String email){
        String key =  "E/"+email;
        return getData(key);
    }

    /**
     * 해당 이메일의 인증코드를 생성(기록)합니다.
     *
     * @param email 이메일
     * @param code 인증코드
     */
    public void setEmailCode(String email, String code){
        String key =  "E/"+email;
        setDataExpire(key,code,mailCodeExp);
    }


    /**
     * Redis에서 key로 값을 조회합니다.
     *
     * @param key Redis에서 조회할 key
     * @return 조회된 value 값
     */
    public String getData(String key){
        try {
            ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
            return valueOperations.get(key);
        }catch (Exception e){
            throw new CustomException(ResponseStatus.DATABASE_ERROR);
        }
    }

    /**
     * Redis에서 해당 key를 value로 생성/갱신 합니다.
     *
     * @param key key
     * @param value value
     */
    public void setData(String key, String value){
        try {
            ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
            valueOperations.set(key,value);
        }catch (Exception e){
            throw new CustomException(ResponseStatus.DATABASE_ERROR);
        }
    }

    /**
     * Redis에서 해당 key를 value 값으로 생성/갱신 합니다.
     * 만료시간을 설정할 수 있습니다.
     *
     * @param key key
     * @param value value
     * @param duration 만료시간 (초)
     */
    public void setDataExpire(String key,String value,long duration){
        try {
            ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key,value,expireDuration);
        }catch (Exception e){
            throw new CustomException(ResponseStatus.DATABASE_ERROR);
        }
    }

    /**
     * Redis에서 key로 정보를 제거합니다.
     *
     * @param key key
     */
    public void deleteData(String key){
        try {
            stringRedisTemplate.delete(key);
        }catch (Exception e){
            throw new CustomException(ResponseStatus.DATABASE_ERROR);
        }
    }
}