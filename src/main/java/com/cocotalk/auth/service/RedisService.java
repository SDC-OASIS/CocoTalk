package com.cocotalk.auth.service;

import com.cocotalk.auth.dto.common.ClientType;
import com.cocotalk.auth.dto.common.response.ResponseStatus;
import com.cocotalk.auth.application.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

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

    public String getRefreshToken(ClientType clientType, long userId){
        String key =  "RT/"+clientType.toString()+"/"+userId;
        return getData(key);
    }

    public void setRefreshToken(ClientType clientType, long userId, String refreshToken){
        log.info(clientType.toString());
        String key =  "RT/"+clientType.toString()+"/"+userId;
        log.info("key:"+key+"/"+refreshToken);
        setDataExpire(key,refreshToken,refreshTokenExp);
    }

    public void deleteRefreshToken(ClientType clientType, long userId){
        String key =  "RT/"+clientType.toString()+"/"+userId;
        deleteData(key);
    }

    public String getEmailCode(String email){
        String key =  "E/"+email;
        return getData(key);
    }

    public void setEmailCode(String email, String code){
        String key =  "E/"+email;
        setDataExpire(key,code,mailCodeExp);
    }

    public String getData(String key){
        try {
            ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
            return valueOperations.get(key);
        }catch (Exception e){
            throw new AuthException(ResponseStatus.DATABASE_ERROR);
        }
    }

    public void setData(String key, String value){
        try {
            ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
            valueOperations.set(key,value);
        }catch (Exception e){
            throw new AuthException(ResponseStatus.DATABASE_ERROR);
        }
    }

    public void setDataExpire(String key,String value,long duration){
        try {
            ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key,value,expireDuration);
        }catch (Exception e){
            throw new AuthException(ResponseStatus.DATABASE_ERROR);
        }
    }

    public void deleteData(String key){
        try {
            stringRedisTemplate.delete(key);
        }catch (Exception e){
            throw new AuthException(ResponseStatus.DATABASE_ERROR);
        }
    }
}