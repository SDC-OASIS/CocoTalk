# Cocotalk 채팅 서버

# 목차

- [개발 도구](#개발-도구)
- [기능 설명](#기능-설명)
- [프로젝트 명세](#프로젝트-명세)
    - [배포 방법](#배포-방법)
    - [산출물](#산출물)
        - [API 명세서](#API-명세서)
        - [메시지 토픽](#메시지-토픽)
        - [ERD](#DB-ERD)

---

## ⚒ 개발 도구
- Spring Boot
- Spring Cloud Eureka Client
- MongoDB
- WebSocket
- STOMP
- Kafka
- Mapstrcut

---


## 🖥 기능 설명

### 채팅방
- 1:1, 그룹 채팅방 생성
- roomId로 채팅방 조회
- roomId로 채팅방과 첫 메시지 페이지 조회
- user가 참가중인 채팅방 리스트 조회
- userId와 상대방 userId로 1:1 채팅방 조회
- roomId로 채팅방로 채팅방 삭제

### 메시징
- 채팅 메시지 pub/sub
- 채팅방 초대 pub/sub
- 채팅방 나가기 pub/sub
- AWAKE 메시지 pub/sub
- 채팅 메시지 리스트 Pagination

---

## 🔧 프로젝트 명세

### ️ 배포 방법 

### 환경

- 사용 버전
    - JDK 11

### 추가 설정 및 파일

1. application.yml
    1. /src/main/resources 으로 이동합니다.
    2. 아래와 같은 내용으로 resources 안에 application.yml를 생성합니다.
    
```yaml
server:
  port: 8080

spring:
  profiles:
    group:
      "dev": "common, devdb"
      "prod": "common, proddb"

---

spring:
  config:
    activate:
      on-profile: devdb # optional, but recommend
  data:
    mongodb:
      host: {{ your developing mongodb host }}
      port: {{ your developing mongodb port }}
      database: {{ your developing mongodb database }}
      username: {{ your developing mongodb username }}
      password: {{ your developing mongodb password }}

---

spring:
  config:
    activate:
      on-profile: proddb
  data:
    mongodb:
      host: {{ your production mongodb host }}
      port: {{ your production mongodb port }}
      database: {{ your production mongodb database }}
      username: {{ your production mongodb username }}
      password: {{ your production mongodb password }}

---

spring:
  config:
    activate:
      on-profile: common # common profile
  application:
    name: chat-service # spring cloud eureka applicaiton group name
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher # swagger 세팅
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

logging:
  level:
    org:
      springframework:
        data:
          mongodb:
            core:
              MongoTemplate: DEBUG # mongodb query logging

eureka:
  instance:
    instance-id: {{ eureka client id }}
    hostname: {{ eureka client hostname }}
    ip-address: {{ eureka client ip address }}
    prefer-ip-address: true # optional

client:
  register-with-eureka: true # setting whether to register for Service Discovery
  fetch-registry: true
  service-url:
    defaultZone: {{ eureka server url }} # A client is registered by sending a POST request to it.

jwt:
 secret: {{ your jwt secret }}

cocotalk:
  message-bundle-limit: {{ message bundle limit }}
  message-paging-size: {{ message paging size }} # must be little or equals message-bundle-limit

oci:
  user:
    url: {{ user-server-url/token }}

---

spring:
  config:
    activate:
      on-profile: dev

---

spring:
  config:
    activate:
      on-profile: prod

---

spring:
  profiles:
    active: {{ active profile }}

```

---

## 🎞 산출물

### API 명세서

- 테스트 페이지  `[GET] /`
- user가 참가중인 채팅방 리스트 조회  `[GET] /rooms/list`
- roomId로 채팅방 조회 `[GET] /rooms/{id}`
- roomId로 채팅방과 첫 메시지 페이지 조회 `[GET] /rooms/{id}/tail`
- userId와 상대방 userId로 1:1 채팅방 조회 `[GET] /rooms/private?userid=`
- roomId로 채팅방 삭제 `[DELETE] /rooms/{id}`
- 채팅 메시지 리스트 Pagination `[GET] /messages?roomid={}bundleid={}count={}`

### 메시지 토픽
**Publish**
- 1:1, 그룹 채팅방 생성 `/chatroom/new`
- 채팅 메시지 Publish `/chatroom/{roomId}/message/send`
- 채팅방 초대 Publish `/chatroom/{roomId}/message/invite`
- 채팅방 나가기 Publish `/chatroom/{roomId}/message/leave`
- Awake 메시지 Publish `/chatroom/{roomId}/message/awake`

**Subscribe**
- **채팅방 리스트**
  - 메시지 토픽 `'/topic/' + userId + '/message'`
  - 방 정보 토픽`'/topic/' + userId + '/room'`
  - 방 생성 정보 토픽 `'/topic/' + userId + '/room/new'`
- **채팅방 내부**
  - 메시지 토픽 `'/topic/' + roomId + '/message'`
  - 방 정보 토픽 `'/topic/' + roomId + '/room'`

---

### DB ERD

