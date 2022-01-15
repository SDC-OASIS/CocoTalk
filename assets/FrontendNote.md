# CocoTalk-Frontend 학습내용 정리dd

[TOC]

## 1. Vue.js 소개





## 2. 개발 환경 설정 및 첫 번째 프로젝트



### 프로젝트 생성

#### Vue Router

> 생성시 Router을 추가 하면 history mode를 사용할지 물어본다. 
> default는 해시모드이기 때문에 사용하지 않을경우 router url 뒤에 #가 붙는다.
>
> [참고]https://m.blog.naver.com/bkcaller/221466075921



### eslint와 Prettier 적용하기

> 프로젝트 생성시 vue2 default 대신 직접설정하는 방식으로 진행하였다.
> eslint + prettier 옵션 선택해 진행.

#### 1. eslint로 오류 발생시 브라우저에서는 안보이게 처리하는 법

> vue.config.js파일을 최상단에 만들어준다.

```javascript
module.exports = {
	devServer: {
		overlay: false,
	},
};
```

#### 2. Prettier와 동시에 사용하기

> eslint와 함께 사용할 경우, 따로 prettierrc.js로 파일을 빼지 않고 eslintrc.js에 함께 설정해주어야 충돌을 막을 수 있다.

```json
 ...
 rules: {
    "no-console": "off",
    "prettier/prettier": ['error', {
      "semi": true,
      "useTabs": true,
      "tabWidth": 2,
      "trailingComma": "all",
      "printWidth": 120,
      "endOfLine": "auto"
    }]
  },
  ...
```

##### useTabs로 인한 에러

> 탭을 사용하는 것으로 설정하였는데, vscode setting에서 스페이스바를 허용하게 한다면 충돌이 일어날 수 있다. 따라서 설정에서 indentation 검색 -> Insert Spaces 체크해제를 해주어야한다.

##### 공백에서 빨간줄이 그어지고 에러가 나온다면?

`Replace '··' with '↹'` 에러 발생

> 모든 공백을 탭으로 설정해주었기에 스페이스바로 작성된 경우 에러가 뜨는 것이다.
>
> 이 때에는 모든 스페이스바를 탭으로 바꿔주면 되는데, 하단에 tab 크기를 클릭하면 변환을 할 수 있는 옵션이 나와 해결이 가능하다.

##### vscode에서 eslint 에러 표시가 안 된다면?

> settings.json에서 설정을 다음과같이 추가해주면 된다.

```json
...
 "editor.codeActionsOnSave": { "source.fixAll.eslint": true },
    "eslint.workingDirectories": [ {"mode": "auto"} ],
...
```



##### Settings.json 에러 밑줄

> 해결하지 않아도 무방하지만 신경쓰여서 해결방법을 찾아보았다.
> default 지정이 되어있는데 지정을 또 해줘서 생기는 문제이다.
>
> 다음과 같이 지정값을 없애주면 해결

[참고] https://www.inflearn.com/questions/29402

```json
"eslint.validate": [
	"vue",
	"javascript",
	"javascriptreact",
	"typescript",
	"typescriptreact",
],
```



##### 유용한 rule

1. 저장시 자동으로 공백 줄맞춤

   [참고] https://whitepro.tistory.com/238

   ```json
   ...
   "editor.formatOnSave": true,
       "editor.formatOnType": true
   ...
   ```

   

## 3. Router

>  routing은 웹 페이지간 이동 방법으로 SPA에 주로 사용.
> 서버에 요청 없이 해당 페이지를 미리 받아 놓고 화면을 갱신하는 형태이다.

### 🍀구현시 고려사항

메신저의 편의성을 위해 전체화면에서 네브바를 제외하면 크게 두 부분으로 나누어지도록 구성하였다.
"친구목록/채팅방목록 & 선택한 채팅방(해당 위치는 설정과같은 정보도 나오도록 사용)"

=> 이 때, 채팅방이 열려져있는 상태라면 친구목록으로 이동하였을 때도 해당 방이 유지되어야한다. 새로 rendering될 경우 현재 보고 있는 화면과도 달라지고 불필요한 rendering이 추가 된다.

* 화면의 큰 두 부분의 url이 사용성있게 변화해야한다.

  ```javascript
  "/friends" : 친구목록 / 채팅시작 전 화면
  "/chats" : 채팅방목록 / 채팅시작 전 화면
  "/friends/chat/chatId" : 친구목록 / 특정 채팅화면 
  "/chats/chat/chatId" : 채팅방목록 / 특정 채팅화면 
  ```

* 화면의 큰 두 부분이 각각 독립적으로 rendering되어야한다.

### 동적 라우팅(Q)

<h5 style="background-color: #f1f8ff">좀 더 생각해보기</h3> 

"좀 더 생각해보기"

✨root와 params가 아니라 해당 url의 구역에 맞는 component를 지정해서인지 url이 빠귀어도 component가 동일하다면 rendering이 이루어지지 않는다! 

=> 그래서 동적 라우팅은 안되는게 아닐까? 시간되면 test해보자.

=> 그것보다는 default일때, setting일때를 각각 지정을 못해서가 아닐까?

https://router.vuejs.org/kr/guide/essentials/dynamic-matching.html



### Nested Router(중첩 라우터)

> 페이지 이동시 다수의 컴포넌트를 동시에 부를 수 있다.

* 부모컴포넌트가 자식 컴포넌트를 가지는 형태로 하위 컴포넌트가 종속되게된다.
* 상위 root가 바뀌면 하위 root 역시 갱신된다.

=> 고려사항과 비교했을 때 부합하지 않는다.

```javascript
var routes = [
      {
        path: '/users',
        component:User,
        children:[
          {
          path: 'posts',
          component:UserPost
        },
        {
        path: 'profile',
        component:UserProfile
      },
        ]
      }
    ];
```



### Named View

> 특정 페이지로 이동했을 때 다수의 컴포넌트가 독립적으로 같은 위치에 동시에 표현되는 방법이다.
>
> [참고] https://beomy.tistory.com/71



* 화면이 크게 두 부분으로 나뉘어지기 때문에 각 부분을 left와 right로 구분했다.

> App.js

```javascript
<router-view name="left" />
<router-view name="right" />
```

* url은 크게 6가지로 나뉘어진다.
  친구목록 + 채팅방 시작전 / 친구목록 + 채팅방열림 / 친구목록 + 설정열림
  채팅방목록 + 채팅방 시작전/ 채팅방목록 + 채팅방열림 / 채팅방목록 + 설정열림
  따라서 각 url변화에 따라 화면의 해당구역에 들어갈 component를 지정했다.

✨root와 params가 아니라 해당 url의 구역에 맞는 component를 지정해서인지 url이 빠귀어도 component가 동일하다면 rendering이 이루어지지 않는다! 
=> 열린 채팅방을 유지할 수 있다는 장점!

```javascript
const routes = [
	{
		path: "/friends",
		name: "friends",
		components: {
			left: Friends,
			right: ChatDefault,
		},
	},
	{
		path: "/chats",
		name: "chats",
		components: {
			left: Chats,
			right: ChatDefault,
		},
	},
	{
		path: "/friends/chat/:roomId?",
		name: "friendsChat",
		components: {
			left: Friends,
			right: Chat,
		},
	},
	{
		path: "/chats/chat/:roomId?",
		name: "chatsChat",
		components: {
			left: Chats,
			right: Chat,
		},
	},
	{
		path: "/friends/setting",
		name: "friendsSetting",
		components: {
			left: Friends,
			right: Setting,
		},
	},
	{
		path: "/chats/setting",
		name: "chatsSetting",
		components: {
			left: Chats,
			right: Setting,
		},
	},
```



#### 채팅방 이동(Q)

> 위에 지정해준 경로로 이동해준다.

```
this.$router.push({ name: "chatsChat", params: { chat: "chat", roomId: roomId } }).catch(() => {});
```

> params값이 변경되면 새로운 방에 들어온 것이라고 판단한다.

❗`$route.~~`로 바로 받아와야한다. this아님! => this에 대해서 좀더 공부해보자.
  `.`을 타고 타고 들어가는 경우 `""`을 꼭 해줘야한다는데... 이것도 좀더 공부하자.

```javascript
"$route.params.roomId": function () {
			console.log("채팅을 시작합니다.");
			// vuex에 마지막 페이지 방문 저장
			this.$store.dispatch("changePage", { chat: this.$route.params.chat, roomId: this.$route.params.roomId });
		},
```

<h5 style="background-color: #f1f8ff"> ❓궁금한점</h3>


이상하게 `$route.params.roomId`를 data 변수에 담고 해당 변수를 watch하면 변화를 감지하지 못한다.





# CSS

### 배경 등록

#### 알 수 없는 상화좌우의 흰색 여백 발생 + 스크롤 생성

https://9aram.tistory.com/23



### iconify 활용

index.html body하단에 설치

```javascript
<script src="https://code.iconify.design/2/2.1.0/iconify.min.js"></script>
```



아이콘의 크기는 `font-size`로 변경해줘야한다.

https://velog.io/@leyuri/iconify-%EC%97%90%EC%84%9C-icon-size-%EB%B3%80%EA%B2%BD-%EB%B0%A9%EB%B2%95



### 재사용가능한 css component 구현

https://coonihong.tistory.com/33



### 이미지 없을 때, 액박뜰 때 대체이미지 출력

https://kmhan.tistory.com/234

https://velog.io/@dragoocho/Vue.js-img%EC%97%90%EC%84%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80%EA%B0%80-%EC%B6%9C%EB%A0%A5%EB%90%98%EC%A7%80-%EC%95%8A%EC%9D%84-%EB%95%8C-%EB%8C%80%EC%B2%B4%EC%9D%B4%EB%AF%B8%EC%A7%80%EB%A5%BC-%EC%93%B0%EB%8A%94-%ED%8C%81



### 스쿼클

https://velog.io/@roghabo/Squircle-%EC%8A%A4%EC%BF%BC%ED%81%B4



## 기능 구현

### 이미지 자르기 구현

https://wiznxt.tistory.com/606



