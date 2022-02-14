# 코드 가이드 문서

## 폴더 구조

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%ED%8F%B4%EB%8D%94%EA%B5%AC%EC%A1%B0.png?raw=true" alt="Screen Shot 2022-02-14 at 8.14.02 AM" width="33%" />



## 네트워킹 (Moya 기반)

RxMoya를 사용해서 네트워킹을 비동기로 처리하도록 했습니다. 네트워킹 리스폰스를 각 뷰모델에서 구독한 후 값을 받아 처리합니다. 

- [API Response 구조체](https://github.com/SDC-OASIS/CocoTalk-iOS/tree/master/CocoTalk/CocoTalk/Core/Networking/APIResult)
- [API 엔드포인트 Enum](https://github.com/SDC-OASIS/CocoTalk-iOS/tree/master/CocoTalk/CocoTalk/Core/Networking/API)
- [API를 호출하는 Repository 클래스](https://github.com/SDC-OASIS/CocoTalk-iOS/tree/master/CocoTalk/CocoTalk/Core/Repository)

## 기능

- [로그인/회원가입](https://github.com/SDC-OASIS/CocoTalk-iOS/tree/master/CocoTalk/CocoTalk/Scenes/Auth)
  회원가입 하는 과정에서 나타나는 각각의 뷰와 뷰모델이 있습니다.

- [토큰 관리](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/CocoTalk/CocoTalk/Core/Extension/KeyChainWrapper%2BExt.swift)

  - 액세스/리프레시/FCM 토큰 enum으로 관리

- [내 기본 정보 관리](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/CocoTalk/CocoTalk/Core/Enum/UserDefaultsKeyEnum.swift)

  - UserDefaults에 사용자 정보를 저장해서 API 호출을 다시 안하게 함

- [카카오톡 메시지 UI 구현](https://github.com/SDC-OASIS/CocoTalk-iOS/tree/master/CocoTalk/CocoTalk/Scenes/ChatRoom)

  - UICollectionViewLayout를 커스터마이징해서 사용

- 동시 로그인 방지 기능

  - [소켓으로 로그아웃 신호를 받아서 로그아웃 하는 로직](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/e2e287ead6e11d97997aa533b957a31821f5ccaf/CocoTalk/CocoTalk/Core/Helpers/WebSocketHelper.swift#L157)
  - [앱이 foreground로 돌아올 때 토큰을 검증 후 로그아웃 시키는 로직](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/e2e287ead6e11d97997aa533b957a31821f5ccaf/CocoTalk/CocoTalk/Application/SceneDelegate.swift#L47)
  - [앱이 켜질 때 스플래시 뷰에서 토큰을 검증 후 로그아웃 시키는 로직](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/e2e287ead6e11d97997aa533b957a31821f5ccaf/CocoTalk/CocoTalk/Scenes/Splash/SplashViewController.swift#L42)

- 액세스 토큰 재발급 로직

  - [앱이 켜질 때 스플래시 뷰에서 토큰을 검증 하는 로직](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/e2e287ead6e11d97997aa533b957a31821f5ccaf/CocoTalk/CocoTalk/Scenes/Splash/SplashViewController.swift#L42)
  - [액세스 토큰이 만료됐을 경우 재발급하는 로직](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/e2e287ead6e11d97997aa533b957a31821f5ccaf/CocoTalk/CocoTalk/Scenes/Splash/SplashViewModel.swift#L72)

- RxMoya를 활용한 네트워킹

  - [폴더](https://github.com/SDC-OASIS/CocoTalk-iOS/tree/master/CocoTalk/CocoTalk/Core/Repository)
  - [개별 파일](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/CocoTalk/CocoTalk/Core/Repository/AuthRepository.swift)

- [StompClientLib를 활용한 채팅 소켓 Helper](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/CocoTalk/CocoTalk/Core/Helpers/WebSocketHelper.swift)

  

