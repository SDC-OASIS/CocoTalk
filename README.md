# <img height="45px" width="45px" src="https://cocotalk.s3.ap-northeast-2.amazonaws.com/common/logo.png"> CocoTalk



# 목차

- [코코톡이란?](#-코코톡이란) 
- [디렉토리 구조](#-디렉토리-구조)
- [프로젝트 설명](#-프로젝트-설명)
- [프로젝트 기능](#-프로젝트-기능)
- [전체 아키텍처](#-전체-아키텍처)
- [프로젝트 역할](#-프로젝트-역할)
- [산출물](#-산출물)
  - [API 명세서](#api-명세서)
  - [기능 정의서](#기능-정의서)
  - [다이어그램](#다이어그램)

- [Github Repository](#-github-repository)

---



## <img height="40px" width="40px" src="https://cocotalk.s3.ap-northeast-2.amazonaws.com/common/logo.png"> 코코톡이란?

- 카카오톡의 디자인과 기능을 참고하여 만들어진 채팅 웹/앱입니다.
- MSA 구조로 확장 가능하며 Faliover를 고려해 개발했습니다.

---



##  📂디렉토리 구조

```
├── docs
├── resources
├── scripts
└── src
```

- `/docs` : 프로젝트에 관련된 문서 (API 명세서, 기능 정의서, DB 다이어그램)
- `/scripts` :  DB 스키마 스크립트
- `/resources` : 이미지 및 필요 자원들
- `/src`: 프로젝트가 담긴 디렉토리


---



## 💼 프로젝트 설명



#### 클라이언트

- [iOS](src/client/ios/README.md)

- [WEB](src/client/web/README.md)

#### 서버

- [인증 서버](src/server/CocoTalk-Auth/README.md)

- [유저 서버](src/server/Cocotalk-User/README.md)

- [채팅 서버](src/server/Cocotalk-Chat/README.md)

- [푸시 서버](src/server/CocoTalk-Push/README.md)

- [API GateWay](src/server/Cocotalk-Cloud/README.md)

- [채팅 서버 관리 서버](src/server/Cocotalk-Presence/README.md)

  

---



## 🖥 프로젝트 기능

**자세한 내용은 [기능 정의서](#기능-정의서)를 참고해주세요**

#### 유저 

- ##### 회원가입

  - 서비스 이용 동의
  - SMS 인증
  - Email 인증

- ##### 로그인

  - 인증 토근 관리
  - FCM 토큰 관리
  - 기기별 동시 접속 제한

#### 프로필

- 내 상태 메시지 편집
- 내 프로필 사진 편집
- 내 배경 사진 편집
- 내 친구들 프로필 목록 조회
- 친구 프로필 상세 조회

#### 친구

- 친구 검색
- 친구 추가
- 친구 목록 조회

#### 채팅방

- 채팅방 목록 조회
- 개인 채팅방 생성
- 단체 채팅방 생성
- 단체 채팅방 프로필 생성
- 채팅방 초대
- 채팅방 나가기
- 채팅방 서랍
- 채팅방 읽지 않은 메시지 수 표시

#### 채팅

- 메시지 송수신
  - 일반 메시지
  - 사진 메시지
  - 동영상 메시지
  - 파일 메시지
- 메시지를 읽지 않은 사람 수 표시
- 이전 메시지 내역 사져오기

#### 푸시

- 푸시 알림 송수신
- 푸시 알림 뮤트

  
  

---



## 🔎 전체 아키텍처

![전체 아키텍처](http://stove-developers-gitlab.sginfra.net/stove-dev-camp-2nd/oasis/-/raw/main/docs/%EC%A0%84%EC%B2%B4_%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98_%EA%B5%AC%EC%A1%B0.png)


---



## 👩 프로젝트 역할



### 고병학

- 팀장
- iOS 개발 
- 실제 카카오톡과 유사한 채팅방 UI를 구현



### 권희은 

- Frontend 개발 
- 실제로 존재하지 않는 카카오톡의 Web 버전을 사용성을 고려해 설계 및 구현

  

### 김민정

- Backend 개발
  - Auth 서버
  - Push 서버
  - S3 파일 업로드 관련 API들  (유저 프로필 및 채팅에 사용되는 API) 
  - 서버간 Kafka로 연결 
- Frontend 개발 보조

### 황종훈 

- Backend 개발
  - Chat 서버
  - User 서버
  - Gateway 서버
  - 채팅 서버 관리 서버
  - 서버 성능 테스트 및 개선


---



## 📜 산출물



### API 명세서

명세서 페이지 오른쪽 상단의 버튼으로 Server를 변경할 수 있습니다
- [API 명세서](http://138.2.88.163:8000/webjars/swagger-ui/index.html)



### 기능 정의서

![기능정의서](http://stove-developers-gitlab.sginfra.net/stove-dev-camp-2nd/oasis/-/raw/main/docs/%EA%B8%B0%EB%8A%A5%EC%A0%95%EC%9D%98%EC%84%9C.png)



### 다이어그램

- **MySQL ERD**

![MySQL ERD](http://stove-developers-gitlab.sginfra.net/stove-dev-camp-2nd/oasis/-/raw/main/docs/MySQL_ERD.png)

- **MongoDB**

![MySQL ERD](http://stove-developers-gitlab.sginfra.net/stove-dev-camp-2nd/oasis/-/raw/main/docs/MongoDB_Diagram.png)



---



## 📌 Github Repository 

- [Oasis Github Organizations](https://github.com/SDC-OASIS)

  

---
