# CocoTalk-iOS

### 코드 가이드 문서

https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/CodeGuide.md



## Getting Started

### Installing

CocoTalk 프로젝트를 받고 실행할 수 있는 방법

```bash
git clone git@github.com:SDC-OASIS/CocoTalk-iOS.git

cd CocoTalk
# Install dependencies from Podfile
pod install
open CocoTalk.xcworkspace
# You can edit codes and run build.
```





## Screenshot

### 동시 로그인 방지

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/동시로그인방지.gif?raw=true" alt="동시로그인방지" style="zoom:67%;" />

**왼쪽과 오른쪽기기가 같은 아이디로 로그인 되는 모습입니다. 오른쪽이 더 최근에 로그인된 기기이므로 왼쪽은 알림경고창이 보인 후 로그아웃 됩니다.**

#### 같은 아이디로 다른 기기에서 로그인했을 경우 아래 3가지 방법으로 자동 로그아웃 됩니다.

- 앱이 켜져있고, 소켓에 연결되어 있을 때 현재 기기 검증 후 로그아웃
- 앱 새로 켜질 때 스플래시 뷰에서 현재 기기 검증 후 로그아웃
- 앱이 백그라운드에서 포그라운드로 전환될 때 현재 기기 검증 후 로그아웃


---



### 카카오톡 채팅방 UI 구현 중
**카카오톡과 비슷한 채팅 UI 구현을 위해 UICollectionViewLayout을 커스텀한 MessageCollectionViewLayout를 만들어 사용했습니다.**

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EC%B1%84%ED%8C%85%EB%B0%A9.gif?raw=true" alt="채팅방" width="33%" />


---


### 친구 추가

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EC%B9%9C%EA%B5%AC%EC%B6%94%EA%B0%80.gif?raw=true" alt="친구추가" width="33%" />



---


### 채팅방 생성

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EC%B1%84%ED%8C%85%EB%B0%A9%20%EC%83%9D%EC%84%B1.gif?raw=true" alt="채팅방 생성" width="33%" />



## Architecture

MVVM으로 구현되어 있으며 각 기능들에 대한 아키텍처 다이어그램은 개발이 완료된 후 업로드 예정입니다.



## Tech stack & Libraries

StompClientLib만 Cocoapod을 사용하고 나머지는 Swift Package Manager를 사용합니다.

- [IQKeyboardManager](https://github.com/hackiftekhar/IQKeyboardManager) ~> 6.0.0
- [SnapKit](https://github.com/SnapKit/SnapKit) ~> 5.0.0
- [Then](https://github.com/devxoul/Then) ~> 2.0.0
- [RxSwift](https://github.com/ReactiveX/RxSwift) ~> 6.0.0
- [Kingfisher](https://github.com/onevcat/Kingfisher) ~> 7.0.0
- [Moya](https://github.com/Moya/Moya) ~> 15.0.0
- [SwiftKeychainWrapper](https://github.com/jrendel/SwiftKeychainWrapper)  ~> 4.0.0
- [firebase-ios-sdk](https://github.com/firebase/firebase-ios-sdk.git) ~> 8.0.0
- [StompClientLib](https://github.com/WrathChaos/StompClientLib) ~> 1.4.1
