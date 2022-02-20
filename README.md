# CocoTalk-Push

[코드 가이드 문서](https://github.com/SDC-OASIS/CocoTalk-Push/blob/develop/docs/%EC%BD%94%EB%93%9C%20%EB%A6%AC%EB%B7%B0%20%EA%B0%80%EC%9D%B4%EB%93%9C%20%EB%AC%B8%EC%84%9C.md)

# 목차

- [사용 기술](#-사용-기술)   
- [기능 설명](#-기능-설명)
- [실행 방법](#-실행-방법)
- [API 명세서](#-api-명세서)

---

## ⚒ 사용 기술

- Spring WebFlux
- Spring Cloud Eureka Client
- Kafka
- R2DBC
- MySQL
- Firebase 클라우드 메시징

---

## 🖥 기능 설명

### client 정보에 따라 mobile/web으로 별개로 관리

- **device 관리**
  - 조건으로 device 정보 찾기
    - userId로 찾기
    - userId + client type으로 찾기
  - device 갱신하기
    - client 정보를 통해 mobile/web 구분하여 갱신
  - device 삭제하기
  
- **push**
  - Kafka를 사용하여 chat 서버에서 채팅 메시지가 올 때마다 기기에게 PUSH 보내기
  - FCM을 사용하여 특정 기기에게 PUSH 보내기
  
---

## 🔧 프로젝트 명세

### ️실행 방법

### 환경

- 사용 버전	
  - JDK 11

### 추가 설정 및 파일
   
- application.yml

```yaml
spring:
  application:
    name: push-service
  r2dbc:
    url: {{ your database url }}
    username: {{ your database username }}
    password: {{ your database password }}
  kafka:
    topic: push
    consumer:
      bootstrap-servers: {{ your kafka server }}
      group-id: push-server
      auto-offset-reset: latest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      bootstrap-servers: {{ your kafka server }}
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

eureka:
  instance:
    instance-id: {{ eureka client id }}
    hostname: {{ eureka client hostname }}
    ip-address: {{ eureka client ip address }}
    prefer-ip-address: true # optional

  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: {{ eureka server url }} # A client is registered by sending a POST request to it.

fcm:
  api-url: {{ your fcm api call url }}
```

---

## 📜 API 명세서

#### 명세서 바로 보기

- kafka 관련 api는 테스트 용입니다.

- [API 명세서](http://138.2.88.163:8000/webjars/swagger-ui/index.html?urls.primaryName=push)

---
