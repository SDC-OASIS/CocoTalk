package com.cocotalk.presence.service;

import com.cocotalk.presence.exception.CustomError;
import com.cocotalk.presence.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatConnectService {
    private final RedisTemplate<String, Integer> redisTemplate;

    private static final PriorityQueue<Connection> chatConnections = new PriorityQueue<>();

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Connection implements Comparable {
        public String url;
        public Integer noc; // numberOfClients

        @Override
        public int compareTo(Object obj) {
            Connection connection = (Connection) obj;
            return this.noc - connection.noc; // connection 적은 순으로 정렬
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            else if (!(obj instanceof Connection)) return false;
            Connection connection = (Connection) obj;
            return connection.url.equals(this.url);
        }
    }

    public void registerConnectionUrl(String serverUrl) {
        String key =  "CC/" + serverUrl;
        setData(key, 0);
        log.info("serverUrl {} Redis에 등록됨.", serverUrl);
    }

    public void withdrawConnectionUrl(String serverUrl) {
        String key =  "CC/" + serverUrl;
        deleteData(key);
        log.info("serverUrl {} Redis에서 제거됨.", serverUrl);
    }

    public void connectChatServer(String serverUrl) {
        String key =  "CC/" + serverUrl;
        redisTemplate.opsForValue().increment(key);
        log.info("serverUrl {} 커넥션 + 1.", serverUrl);
    }

    public void disconnectChatServer(String serverUrl) {
        String key =  "CC/" + serverUrl;
        redisTemplate.opsForValue().decrement(key);
        log.info("serverUrl {} 커넥션 - 1", serverUrl);
    }

    public String handoverConnectionUrl() {
        String prefix =  "CC/*";
        Set<String> urls = redisTemplate.keys(prefix);
        if(urls == null) throw new CustomException(CustomError.CHAT_SERVER_CONNECTION, "연결할 수 있는 채팅 서버가 없습니다");
        ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
        for(String key : urls) {
            Integer numberOfClients = valueOperations.get(key); // multiGet으로 개선 가능
            key = key.replaceFirst(prefix, "");
            log.info("URL로 변환된 Redis key : {}", key);
            chatConnections.add(new Connection(key, numberOfClients));
        }
        String leastConnectionUrl = chatConnections.peek().url;
        chatConnections.clear();
        return leastConnectionUrl;
    }

    public void setData(String key, Integer value){
        try {
            ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) { // 그냥 Exception만 있는 것은 CustomException 날려주기
            e.printStackTrace();
            throw new CustomException(CustomError.REDIS, e);
        }
    }

    public void deleteData(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) { // 그냥 Exception만 있는 것은 CustomException 날려주기
            e.printStackTrace();
            throw new CustomException(CustomError.REDIS, e);
        }
    }
}
