# Cocotalk 채팅 관리 서버

# 목차

- [개발 도구](#개발-도구)
- [기능 설명](#기능-설명)
- [프로젝트 명세](#프로젝트-명세)
    - [배포 방법](#배포-방법)
    - [산출물](#산출물)
        - [API 명세서](#API-명세서)

---

## ⚒ 개발 도구
- Spring Boot
- Spring Cloud Eureka Client
- Redis
- WebSocket

---


## 🖥 기능 설명

### 채팅 서버 - 클라이언트 간 커넥션 관리
- 채팅 서버 기동 시 STOMP Connect Endpoint 등록
- 채팅 서버 종료 시 STOMP Connect Endpoint 제거
- 클라이언트가 STOMP Connect 전에 프리젠스 서버에 요청하여 leastConnection 상태인 채팅 서버의 Endpoint 제공

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
  application:
    name: presence-service
  redis:
    host: {{ your production redis host }}
    port: {{ your production redis port }}
    password: {{ your production redis password }}

logging:
  level:
    org:
      hibernate:
        type:
          descriptor:
            sql: trace

jwt:
  secret: {{ your jwt secret key }}

oci:
  user:
    url: {{ your user info api }}

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

```

---

## 🎞 산출물

### API 명세서

**[Swagger UI API Docs 바로가기](http://138.2.88.163:8000/webjars/swagger-ui/index.html?urls.primaryName=presence)**

- STOMP Connect할 채팅 서버 Enpoint 조회  `[GET] /stomp/connect`
