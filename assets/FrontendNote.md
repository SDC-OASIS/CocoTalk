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



### html 속성별 줄바꿈 일어나는 현상

> Eslint에 설정한 prettier이 html에서 먹히지 않는 것. 
> Default Formatter을 eslint로 설정해 해결하였다.

https://yjg-lab.tistory.com/91



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
>  서버에 요청 없이 해당 페이지를 미리 받아 놓고 화면을 갱신하는 형태이다.

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



## 4. Vuex

### Modal 상태관리

> [도입하게 된 이유]
> 현재 이중 분할 화면 template을 구현 중인데, 이 경우 최하위 컴포넌트에서 클릭했을 때 전체화면에 꽉차는 형태의 모달을 구현하고 싶다. 그렇다면 Modal은 최상위 분할 이전 컴포넌트에 있어야하는데, 그렇다면 클릭 이벤트 emit이 계속해서 발생한다. 이를 보다 효율적으로 관리하기 위해 Modal의 상태를 전역에서 관리해주는 것이 좋지 않을까 하는 생각을 하게 되었다. 
> [참고]
> Dynamic Reusable Vuex : https://jeongwooahn.medium.com/%EB%AA%A8%EB%8B%AC-%EB%A0%88%EC%9D%B4%EC%96%B4%ED%8C%9D%EC%97%85-%EC%BB%B4%ED%8F%AC%EB%84%8C%ED%8A%B8-%EB%A7%8C%EB%93%A4%EA%B8%B0-with-dynamic-reusable-vuex-c99e611c6133



#### ❓vuex 내부 데이터 값을 fals로 지정해둔 경우 데이터 변경시 에러

>  `"TypeError: Cannot create property 'status' on boolean 'false'"`
>
> false로 지정해둔 모달 상태값에  true를 넣어 변경하려하니 에러가 발생했다.
> 따라서 String으로 open과 close를 나누어주었다.
>
> => data를 false값으로 지정해두면 변경이 안되는걸까?

https://stackoverflow.com/questions/55670250/how-to-fix-cannot-create-property-default-on-boolean-true/56566624



#### ❗Dump Data에 name을 key로 넣었을 때 새로고침시 name이 사라지는 현상

> name => username으로 키 이름을 변경해주니 해결되었다. 

아마 Javascript 예약어 name 때문이 아닐까 하는 생각이든다.

`"name"` 과 같이 예약어는 큰따옴표에 넣어주어 사용해야한다고한다. => 되도록 이런 경우에는 사용하지 않는게 좋지 않을까?

[참고] https://ru-pert.tistory.com/19



## 5. CSS

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



### felx box에서 한줄 띄기

https://tobiasahlin.com/blog/flexbox-break-to-new-row/

```vue
<div class="chat-default-container row">
    <div>
        <img src="../images/chat_default_mococo.png" alt="" />
    </div>
    <div class="break"></div>
    <div class="padding-bottom: 100px;">채팅을 시작해보세요.</div>
</div>
```



### position: fixed 사용시 부모 width 받아오지 못함

> width: inherit 으로 해결

https://stackoverflow.com/questions/5873565/set-width-of-a-position-fixed-div-relative-to-parent-div

```css
.message-input-container {
	position: fixed;
	background-color: #ffffff;
	height: 10vh;
	bottom: 0px;
	width: inherit;
}
```



### textarea

https://velog.io/@leemember/CSS-textarea-%ED%81%AC%EA%B8%B0-%EA%B3%A0%EC%A0%95%ED%95%98%EA%B8%B0



### input 클릭했을때 테두리 색상 변경

http://happycgi.com/16392

```css
.login input:focus {
	outline: 2px solid #fce41e;
}
```



### style에 변수 넣기

https://iancoding.tistory.com/213

```html
<div class="modal-container" :style="{ backgroundImage: `url(${userInfo.background})` }" @click.self="openFullImg(userInfo.background)">
			<div @click="closeProfileModal">
				...
```



### checkbox custom

https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=taz0505&logNo=221183561041

```html
<input :id="'checked' + idx" v-model="selectedFriends" type="checkbox" :value="friend.username" />
								<label :for="'checked' + idx"> </label>
```



```css
.wrap input[type="checkbox"] {
	position: absolute;
	width: 10px;
	height: 10px;
	padding: 0;
	margin: -1px;
	overflow: hidden;
	clip: rect(0, 0, 0, 0);
	border: 0;
}
.wrap input[type="checkbox"] + label {
	display: inline-block;
	position: relative;
	padding-left: 26px;
	cursor: pointer;
}
.wrap input[type="checkbox"] + label:before {
	content: "";
	position: absolute;
	top: -15px;
	width: 30px;
	height: 30px;
	text-align: center;
	background: #fff;
	border: 1px solid #ccc;
	box-sizing: border-box;
	border-radius: 20px;
} /* 보여질 부분의 스타일을 추가하면 된다. */
.wrap input[type="checkbox"]:checked + label:after {
	content: "✔";
	text-align: center;
	position: absolute;
	top: -15px;
	width: 30px;
	height: 30px;
	background-color: #fae64c;
	border-radius: 20px;
	color: #ffffff;
	font-size: 20px;
}
```



## 6. Template

### 반복문

> 반복문을 돌리는 template이 item에 해당한다. 이를 감싸려면 반복문 밖에 컨테이너 선언.

```vue
<div class="friend-list-container">
	<div class="friend-container" v-for="(friend, idx) in friends" :key="idx">
		<ProfileImg :imgUrl="friend.profile" width="50px" />
		<FriendListUserInfo :userInfo="friend" />
	</div>
</div>
```



### 재사용 공통 컴퍼넌트 만들기

https://kr.vuejs.org/v2/guide/class-and-style.html



### 모달구현

> `@click.self`로 간단하게 모달외부영역 클릭시 모달이 닫히도록 구현하였다.

```html
<div class="modal row" @click.self="closeProfileModal">
	<div class="modal-container">
		...
	</div>
</div>
```



### SVG

> 카카오톡 프로필모양의 스쿼클을 구현하기위해 사용하였는데, figma의 iconify를 사용할때에도 code는 span이지만 브라우저상에서 svg로 아이콘이 그려지는 것을 확인하였다. 해당 아이콘 부분에서 모달을 띄울때 미세한 깜빡임이 발생하는 것을 파악해 이를 해결하고자 fontawesome으로 대체하였다. 그러니 깜빡임은 일어나지 않았다. 



#### CheckBox구현

> checkbox에 javascript로 Array에 넣어주는 작업을 해야할 것이라 생각했는데 신기하게도 v-model로 자동으로 `selectedFriends` 에 추가되고 삭제된다.

https://mine-it-record.tistory.com/440

```vue
<div class="friend-container row" v-for="(friend, idx) in friends" :key="idx">
	...
	<div class="friend-name" :id="'check' + idx">{{ friend.username }}</div>
	<input v-model="selectedFriends" type="checkbox" :for="'check' + idx" :value="friend.username" @click="selectFriend(friend)" />
</div>
```



### 상단에 새로운 요소 추가시 해당 갯수에 따라 하단컨테이너 크기조절

```html
computed: {
		...
		// 선택한 대화상대 갯수에 따라 친구목록 높이 지정
		height() {
			if (this.selectedFriendsCnt == 0) {
				return "380px";
			} else if (this.selectedFriendsCnt <= 3) {
				return "340px";
			} else {
				return "300px";
			}
		},
	},
```



### 한글 input 바인딩

https://sso-feeling.tistory.com/675



## 7.기능 구현

### 이미지 자르기 구현

https://wiznxt.tistory.com/606



```
<template>
	<div class="nav-outer-container row">
		<div class="nav-logo">
			<img src="@/assets/logo.png" alt="logo" />
		</div>
		<div class="nav-inner-container">
			<ul class="nav row">
				<li>
					<router-link :to="{ path: '/friends/' + roomStatus.roomId }">
						<span class="iconify" data-icon="fa-solid:user-friends"></span>
					</router-link>
				</li>
				<li>
					<router-link to="/chats">
						<span class="iconify" data-icon="ant-design:message-filled"></span>
					</router-link>
				</li>
				<li>
					<router-link to="/friends/setting">
						<span class="iconify" data-icon="bi:bell-fill"></span>
					</router-link>
				</li>
				<li>
					<router-link to="/chats/setting">
						<span class="iconify" data-icon="uil:setting"></span>
					</router-link>
				</li>
				<li>
					<router-link to="/">
						<span class="iconify" data-icon="fe:logout"></span>
					</router-link>
				</li>
			</ul>
		</div>
	</div>
</template>

<script>
import { mapState } from "vuex";

export default {
	computed: {
		...mapState("chat", ["roomStatus"]),

		// ...mapState({
		// 	roomStatus: (state) => state.roomStatus,
		// 	chats: (state) => state.chats,
		// }),
	},
};
</script>
```



