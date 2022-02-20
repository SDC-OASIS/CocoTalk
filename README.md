# Cocotalk API Gateway, Service Discovery

# 목차

- [개발 도구](#개발-도구)
- [기능 설명](#기능-설명)
- [프로젝트 명세](#프로젝트-명세)
    - [배포 방법](#배포-방법)
- [대시보드](#대시보드)

---

## ⚒ 개발 도구

- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Server
- Spring Cloud Eureka Client

---


## 🖥 기능 설명

### API Gateway
- Eureka Client 별 라우팅
- Eureka Client 그룹 별 로드밸런싱

### Service Discovery
- Eureka Client 등록 및 헬스체크

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

### API Gateway

```yaml
server:
  port: 8000

eureka:
  client:
    register-with-eureka: true # setting whether to register for Service Discovery
    fetch-registry: true
    service-url:
      defaultZone: {{ eureka server url }} # A client is registered by sending a POST request to it.

spring:
  application:
    name : api-gateway

  cloud:
    gateway:
      httpclient:
        connect-timeout: {{ connect timeout }}
        response-timeout: {{ response timeout }}
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "*"
            allowedHeaders: "*"
            allowedMethods:
              - POST
              - GET
              - DELETE
              - PUT
              - OPTIONS

      routes:
        # ------------------------------------------ auth-service ----------------------------------------------

        - id: auth-service-device
          uri: lb://auth-service/device
          predicates:
            - Method=GET
            - Path=/auth/device/**
          filters:
            - RewritePath=/auth/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        - id: auth-service-reissue # refresh token 필요
          uri: lb://auth-service/reissue
          predicates:
            - Method=GET
            - Path=/auth/reissue
          filters:
            - RewritePath=/auth/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter
            - RewritePath=/auth/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        - id: auth-service-signout # refresh token 필요
          uri: lb://auth-service/signout
          predicates:
            - Method=GET
            - Path=/auth/signout
          filters:
            - RewritePath=/auth/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/auth/**
          filters:
            - RewritePath=/auth/?(?<segment>.*), /$\{segment}

        # ------------------------------------------ user-service ----------------------------------------------

        - id: user-service-profile
          uri: lb://user-service/profile
          predicates:
            - Method=GET,POST,PUT,PATCH,DELETE
            - Path=/user/profile/**
          filters:
            - RewritePath=/user/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        - id: user-service-friend
          uri: lb://user-service/friend
          predicates:
            - Method=GET,POST,PUT,PATCH,DELETE
            - Path=/user/friends/**
          filters:
            - RewritePath=/user/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - RewritePath=/user/?(?<segment>.*), /$\{segment}

        # ------------------------------------------ chat-service ----------------------------------------------

        - id: chat-service
          uri: lb://chat-service
          predicates:
            - Path=/chat/**
          filters:
            - RewritePath=/chat/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        # ------------------------------------------ push-service ----------------------------------------------

        - id: push-service-device
          uri: lb://push-service/device
          predicates:
            - Method=GET
            - Path=/push/device/**
          filters:
            - RewritePath=/push/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        - id: push-service-delete
          uri: lb://push-service/delete
          predicates:
            - Method=GET
            - Path=/push/delete/**
          filters:
            - RewritePath=/push/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        - id: push-service
          uri: lb://push-service
          predicates:
            - Path=/push/**
          filters:
            - RewritePath=/push/?(?<segment>.*), /$\{segment}

        # ------------------------------------------ presence-service  ----------------------------------------------

        - id: presence-service
          uri: lb://presence-service
          predicates:
            - Path=/presence/**
          filters:
            - RewritePath=/presence/?(?<segment>.*), /$\{segment}
            - JwtAuthenticationFilter

        # ------------------------------------------ end ----------------------------------------------
        - id: openapi
          uri: lb://${spring.application.name}
          predicates:
            - Path=/v3/api-docs/**
          filters:
            - RewritePath=/v3/api-docs/(?<path>.*), /$\{path}/v3/api-docs
            - AddRequestHeader=X-ACCESS-TOKEN, eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ7XCJ1c2VySWRcIjogMTN9IiwiaWF0IjoxNTE2MjM5MDIyfQ.hKSaWJvoCppr2iPfa_Iu6OcAhWrSYdxClqOlK9ituS8

management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  metrics:
    tags:
      application: ${spring.application.name}

jwt:
  secret: {{ your jwt secret }}

```

### Netflix Eureka Server(Service Discovery)
```yaml

server:
  port: 8761 # default port

spring:
  application:
    name: service-discovery

eureka:
  instance:
    hostname: eureka0
  client:
    register-with-eureka: false # The setting whether to operate the server as a client must be set to false.
    fetch-registry: false # It must be set to false as above, otherwise it will register itself for discovery.

```

---

## 🎞 대시보드

**[Eureka Server Dashboard](http://138.2.88.163:8761)**
