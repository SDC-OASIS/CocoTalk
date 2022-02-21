# CocoTalk-iOS

웹소켓 기반 iOS 채팅앱

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

### UICollectionViewLayout을 커스텀해서 카카오톡 채팅방 UI 구현

**카카오톡과 비슷한 채팅 UI 구현을 위해 UICollectionViewLayout을 커스텀한 MessageCollectionViewLayout를 만들어 사용했습니다.**

|                        프로젝트 팀 색                        |                       기존 카카오톡 색                       |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![모바일_채팅방](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EB%AA%A8%EB%B0%94%EC%9D%BC_%EC%B1%84%ED%8C%85%EB%B0%A9.gif?raw=true) | <img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EC%B1%84%ED%8C%85%EB%B0%A9.gif?raw=true" alt="채팅방"/> |

(왼쪽은 팀 프로젝트 색, 오른쪽은 카카오톡 색 적용)

---

### 동시 로그인 방지

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EB%AA%A8%EB%B0%94%EC%9D%BC_%EB%8F%99%EC%8B%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8_%EB%B0%A9%EC%A7%80.gif?raw=true" alt="모바일_동시로그인_방지"  width='80%' />

**왼쪽과 오른쪽기기가 같은 아이디로 로그인 되는 모습입니다. 오른쪽이 더 최근에 로그인된 기기이므로 왼쪽은 알림경고창이 보인 후 로그아웃 됩니다.**

#### 같은 아이디로 다른 기기에서 로그인했을 경우 아래 3가지 방법으로 자동 로그아웃 됩니다.

- 앱이 켜져있고, 소켓에 연결되어 있을 때 현재 기기 검증 후 로그아웃
- 앱 새로 켜질 때 스플래시 뷰에서 현재 기기 검증 후 로그아웃
- 앱이 백그라운드에서 포그라운드로 전환될 때 현재 기기 검증 후 로그아웃

---

### 그 외

|                         채팅방 생성                          |                          친구 추가                           |                    프로필 사진 확인 하기                     |                       프로필 사진 보기                       |
| :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| <img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EC%B1%84%ED%8C%85%EB%B0%A9%20%EC%83%9D%EC%84%B1.gif?raw=true" alt="채팅방 생성" width="100%" /> | <img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EC%B9%9C%EA%B5%AC%EC%B6%94%EA%B0%80.gif?raw=true" alt="친구추가" width="100%" /> | ![모바일_프로필_확인](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EB%AA%A8%EB%B0%94%EC%9D%BC_%ED%94%84%EB%A1%9C%ED%95%84_%ED%99%95%EC%9D%B8.gif?raw=true) | ![모바일_푸시_치킨](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EB%AA%A8%EB%B0%94%EC%9D%BC_%ED%91%B8%EC%8B%9C_%EC%B9%98%ED%82%A8.gif?raw=true) |

## Architecture

### MVVM 활용

데이터를 RxSwift와 RxMoya를 사용해서 비동기 네트워킹을 구현했습니다.   

각 화면에서 데이터를 요청할 때 요청을 보낸 객체에서 결과 값을 subscribe하는 방법으로 구현했습니다. 데이터 요청하는 단계는 ViewController, ViewModel, Repository, Data networking 순서입니다. 

<img src="/images/data_flow.png" alt="data_flow"  width='20%' />



### 소켓 서버에 연결할 경우

채팅을 직접 주고 받는 소켓서버가 2개 이상으로 운영되기 때문에 채팅 관리 서버에서 어떤 소켓 서버로 연결할지 클라이언트에 알려줘야 했습니다. 그래서 클라이언트에서는 채팅 관리 서버에 연결할 소켓 서버 주소를 요청하고, 받은 주소로 소켓 서버에 연결합니다.

![소켓서버연결](https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EC%86%8C%EC%BC%93%EC%84%9C%EB%B2%84%EC%97%B0%EA%B2%B0.png?raw=true)



### 기기별 동시 로그인 방지

프로젝트에서 코코톡 서비스는 모바일 기기 1개, PC 웹 1개에서 총 2개의 기기만을 사용해서 로그인할 수 있습니다. 따라서 모바일 기기 "D"에서 로그인되었을 때 "D"를 제외한 다른 기기들에서는 로그아웃 되어야합니다. 이 기능을 구현한 로직의 순서도는 다음과 같습니다.



#### 로그아웃 될 기기가 백그라운드나 꺼진 상태에서 켜질 때

1. D1 기기에서 아이디/비밀번호로 로그인한다.
   (기기는 백그라운드 상태나 꺼진 상태가 된다.)
2. D2 기기에서 아이디/비밀번호로 로그인한다.
3. D1 기기가 포그라운드 상태가 되면서 "/auth/device" api를 사용해서 현재 갖고 있는 액세스 토큰과 리프레쉬 토큰이 가장 최근 로그인된 디바이스에서 발행한 토큰인지 확인한다.
4. 가장 최근에 로그인된 디바이스에서 발행한 토큰이 아닐 경우 인증 서버에서 유효하지 않은 토큰임을 알려주고, D1 기기가 갖고 있는 계정에 대한 토큰이 삭제되고, 로그아웃 처리된다.

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EB%8F%99%EC%8B%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8_%EC%86%8C%EC%BC%93%EC%97%B0%EA%B2%B0X.png?raw=true"  width='66%' />



#### 로그아웃 될 기기가 켜져있어서 소켓이 연결되어 있을 경우

1) D1 기기에서 아이디/비밀번호로 로그인한다.
   (백그라운드이거나 꺼진 상태가 아닌 포그라운드 상태이다.)
2) D2 기기에서 아이디/비밀번호로 로그인한다.
3) D2를 제외한 다른 모바일 기기에서 유저A로 로그인 된 것을 로그아웃되게끔 소켓 메시지를 보내게 요청
4) 유저A로 로그인된 D2를 제외한 모든 기기에 소켓으로 로그아웃 메시지를 보냅니다.
5) D1은 소켓 메시지를 받고 로그아웃 처리를 수행한다.

<img src="https://github.com/SDC-OASIS/CocoTalk-iOS/blob/master/images/%EB%8F%99%EC%8B%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8_%EC%86%8C%EC%BC%93O.png?raw=true" alt="동시로그인_소켓O" width='66%' />

## Tech stack & Libraries

StompClientLib만 Cocoapod을 사용하고 나머지는 Swift Package Manager를 사용합니다.

- [IQKeyboardManager](https://github.com/hackiftekhar/IQKeyboardManager) ~> 6.0.0
- [SnapKit](https://github.com/SnapKit/SnapKit) ~> 5.0.0
- [Then](https://github.com/devxoul/Then) ~> 2.0.0
- [RxSwift](https://github.com/ReactiveX/RxSwift) ~> 6.0.0
- [Kingfisher](https://github.com/onevcat/Kingfisher) ~> 7.0.0
- [Moya](https://github.com/Moya/Moya) ~> 15.0.0
- [SwiftKeychainWrapper](https://github.com/jrendel/SwiftKeychainWrapper) ~> 4.0.0
- [firebase-ios-sdk](https://github.com/firebase/firebase-ios-sdk.git) ~> 8.0.0
- [StompClientLib](https://github.com/WrathChaos/StompClientLib) ~> 1.4.1
