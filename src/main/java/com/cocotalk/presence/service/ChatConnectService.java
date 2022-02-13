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
    private static class Connection implements Comparable { // 채팅 서버의 STOMP Connetion 정보를 추상화한 클래스
        public String url;
        public Integer noc; // numberOfClients

        @Override
        public int compareTo(Object obj) { // PriorityQueue 정렬을 위한 compareTo 메서드 재정의
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

    public void registerConnectionUrl(String serverUrl) { // [Key : 채팅 서버 URL], [Value: 0] Redis에 등록
        String key =  "CC/" + serverUrl;
        setData(key, 0);
        log.info("serverUrl {} Redis에 등록됨.", serverUrl);
    }

    public void withdrawConnectionUrl(String serverUrl) { // [Key : 채팅 서버 URL], [Value: ?] Redis에서 삭제
        String key =  "CC/" + serverUrl;
        deleteData(key);
        log.info("serverUrl {} Redis에서 제거됨.", serverUrl);
    }

    public void connectChatServer(String serverUrl) { // 채팅 서버에 클라이언트가 STOMP Connect 되었을 때 호출됩니다.
        String key =  "CC/" + serverUrl;
        redisTemplate.opsForValue().increment(key);
        log.info("serverUrl {} 커넥션 + 1.", serverUrl);
    }

    public void disconnectChatServer(String serverUrl) { // 채팅 서버에 클라이언트가 STOMP Disonnect 되었을 때 호출됩니다.
        String key =  "CC/" + serverUrl;
        redisTemplate.opsForValue().decrement(key);
        log.info("serverUrl {} 커넥션 - 1", serverUrl);
    }

    public String handoverConnectionUrl() { // least connection 상태인 채팅서버 URL을 클라이언트에게 전달하는 메서드입니다.
        String prefix =  "CC/*";
        Set<String> urls = redisTemplate.keys(prefix);
        if(urls.size() == 0) throw new CustomException(CustomError.CHAT_SERVER_CONNECTION, "연결할 수 있는 채팅 서버가 없습니다");
        ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
        for(String key : urls) {
            Integer numberOfClients = valueOperations.get(key); // multiGet으로 개선 가능
            key = key.replaceFirst(prefix, "");
            log.info("URL로 변환된 Redis key : {}", key);
            chatConnections.add(new Connection(key, numberOfClients)); // PriorityQueue<Connection> chatConnections
        }
        String leastConnectionUrl = chatConnections.peek().url;
        chatConnections.clear();
        return leastConnectionUrl;
    }

    public void setData(String key, Integer value){
        try {
            ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[ChatConnectService/setData] : Redis에 데이터를 Set 하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.REDIS, e);
        }
    }

    public void deleteData(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[ChatConnectService/deleteData] : Redis에서 데이터를 delete 하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.REDIS, e);
        }
    }
}
