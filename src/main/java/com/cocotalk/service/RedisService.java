package com.cocotalk.service;

import com.cocotalk.response.ResponseStatus;
import com.cocotalk.support.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

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