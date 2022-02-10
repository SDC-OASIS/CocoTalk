# CocoTalk-Auth

# 목차

- [사용 기술](#사용-기술)   
- [기능 설명](#기능-설명)
- [프로젝트 명세](#프로젝트-명세)
  - [실행 방법](#실행-방법)
  - [산출물](#산출물) 
    - [API 명세서](#API-명세서) 


---

## ⚒ 사용 기술

- Spring Boot
- Spring Cloud Eureka Client
- JPA
- Redis
- MySql
- Amazon S3

---

## 🖥 기능 설명

### client 정보에 따라 mobile/web으로 별개 관리
- 회원가입
  - 회원 정보와 프로필 이미지로 회원가입
- 로그인 
  - AccessToken, RefreshToken 발급 후 redis에 저장
  - fcm token을 push server에게 보내 db에 기록함
- 로그아웃
- Token 재발급
  - refresh token을 대조하여 token 재발급
- 이메일 인증
- 동시 로그인 방지 
  - 마지막으로 로그인한 기기가 맞는지 체크 API

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
    name: auth-server
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
  redis:
    host: {{ your redis host }}
    port: {{ your redis port }}
    password: {{ your redis password }}
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: {{ your database url }}
    username: {{ your database username }}
    password: {{ your database password }}
  jpa:
    properties:
      hibernate:
        show_sql: true
        format_sql: true
        use_sql_comments: true
  servlet:
    multipart:
      maxFileSize: 200MB
      maxRequestSize: 200MB

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

mail:
  id: {{ your email }}
  password: {{ password }}
  exp: 300
  smtp:
    auth: true
    port: 465
    starttls:
      required: true
      enable: true
    socketFactory:
      class: javax.net.ssl.SSLSocketFactory
      fallback: false
      port: 465

jwt:
  secret: {{ your jwt secret }}
  token:
    exp:
      access: 3600
      refresh: 1209600

sha256:
  salt: {{ yout salt }}

api:
  push: {{ push-server-url }}


cloud:
  aws:
    credentials:
      accessKey: {{ your credentials accessKey }}
      secretKey: {{ your credentials secretKey }}
    s3:
      bucket: {{ your bucket name }}
    region:
      static: {{ region }}
    stack:
      auto: false
    cloudfront:
      domain: {{ your cloudfront domain }}
```

---

## 🎞 산출물

### API 명세서

#### 명세서 바로 보기

- [API 명세서](http://138.2.88.163:8000/webjars/swagger-ui/index.html?urls.primaryName=auth#/)

---
