package com.cocotalk.presence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatConnectionService {
    private final RedisTemplate<String, Long> redisTemplate;

    public void registerConnectionUrl(String serverUrl) {
        String key =  "CC/" + serverUrl;
        setData(key, 0L);
        log.info("serverUrl {} Redis에 등록됨.", serverUrl);
    }

    public void withdrawConnectionUrl(String serverUrl) {
        String key =  "CC/" + serverUrl;
        deleteData(key);
        log.info("serverUrl {} Redis에서 제거됨.", serverUrl);
    }

    public void setData(String key, Long value){
        try {
            ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteData(String key){
        try {
            redisTemplate.delete(key);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
