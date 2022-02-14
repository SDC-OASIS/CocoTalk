# Cocotalk 채팅 서버

# 목차

- [개발 도구](#개발-도구)
- [기능 설명](#기능-설명)
- [프로젝트 명세](#프로젝트-명세)
    - [배포 방법](#배포-방법)
    - [산출물](#산출물)
        - [API 명세서](#API-명세서)
        - [ERD](#DB-ERD)

---

## ⚒ 개발 도구
- Spring Boot
- Spring Cloud Eureka Client
- MySQL
- AWS S3
- Mapstrcut

---


## 🖥 기능 설명

### 사용자 (유저)
- 유저 조회
- 유저 수정
- 유저 삭제
- 유저 프로필 사진 수정
- 유저 백그라운드 사진 수정
- 유저 프로필 메시지 수정

### 친구
- 친구 추가
- 친구 조회
- 친구 삭제
- 친구 숨김

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
spring:
  profiles:
    group:
      "dev":  "devdb, common"
      "prod": "proddb, common"

---

spring:
  config:
    activate:
      on-profile: devdb # optional, but recommend
  application:
    name: cocotalk-user
  datasource:
    url: jdbc:h2:mem:testdb # h2 in-memory db
  h2:
    console:
      enabled: true

---

spring:
  config:
    activate:
      on-profile: proddb
  application:
    name: cocotalk-user
  datasource:
    url: {{ your mysql host }}
    username: {{ your mysql username }}
    password: {{ your mysql password }}

---

spring:
  application:
    name: user-service # spring cloud eureka applicaiton group name
  config:
    activate:
      on-profile: common
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher # swagger api-docs ant path setting
  jpa:
    show-sql: true
    open-in-view: false
    hibernate:
      ddl-auto: update
    properties:
      dialect: org.hibernate.dialect.MySQL5InnoDBDialect
  servlet: 
    multipart:
      maxFileSize: {{ your multipart max file size }}
      maxRequestSize: {{ your multipart max request size }}

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
    
cloud:
  aws:
    credentials:
      accessKey: {{ your aws credentials access key }}
      secretKey: {{ your aws credentials secret key }}
    s3:
      bucket: {{ your aws s3 bucket }}
    region:
      static: {{ your aws region }}
    stack:
      auto: false
    cloudfront:
      domain: {{ your aws cloudfront domain }}


jwt:
  secret: {{ your jwt secret }}

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

**[Swagger UI API Docs 바로가기](http://138.2.88.163:8000/webjars/swagger-ui/index.html?urls.primaryName=user)**

### 사용자 (유저)
- 유저 전체 조회 `[GET] /profile`
- 유저 Aceess Token으로 조회 `[GET] /profile/token`
- 유저 PK로 조회 `[GET] /profile/{id}`
- 유저 코코톡 Id(cid)로 조회 `[GET] /cid/{cocotalkId}/`
- 유저 email로 조회 `[GET] /email/{adderess}`
- 유저 연락처로 조회 `[GET] /phone?phones=[]`
- 유저 수정 `[PUT] /profile`
- 유저 프로필 사진 수정 `[PUT] /profile/img`
- 유저 백그라운드 사진 수정`[PUT] /profile/bg`
- 유저 프로필 메시지 수정 `[PUT] /profile/message`
- 유저 삭제 `[DELETE] /profile`

### 친구
- 친구 추가 `[POST] /friends`
- PK 리스트로 친구 여러명 추가 `[POST] /friends/list`
- 친구 전체 조회 `[GET] /friends`
- 친구 코코톡 Id(cid)로 조회 `[GET] /friends/cid/{cocotalkId}`
- 친구 삭제 `[DELETE] /friends/{id}`
- 숨긴 친구 조회 `[GET] /friends/hidden`
- 숨긴 친구 수정 `[PATCH] /friends/hidden`

---

### DB ERD
![cocotalk-user-erd](https://user-images.githubusercontent.com/54519245/153526031-9dc0f6ad-2d8b-48bb-af0a-c91d710cfd64.PNG)
