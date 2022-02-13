# Trounble Shooting




## 1. Router

메신저의 편의성을 위해 전체화면에서 네브바를 제외하면 크게 두 부분으로 나누어지도록 구성하였다.
"친구목록/채팅방목록 & 선택한 채팅방(해당 위치는 설정과같은 정보도 나오도록 사용)"

=> 이 때, 채팅방이 열려져있는 상태라면 친구목록으로 이동하였을 때도 해당 방이 유지되어야한다. 새로 rendering될 경우 현재 보고 있는 화면과도 달라지고 불필요한 rendering이 추가 된다.

* 화면의 큰 두 부분의 url이 사용성있게 변화해야한다.

  ```javascript
  "/friends" : 친구목록 / 채팅시작 전 화면
  "/chats" : 채팅방목록 / 채팅시작 전 화면
  "/friends/chat/{chatId}" : 친구목록 / 특정 채팅화면 
  "/chats/chat/{chatId}" : 채팅방목록 / 특정 채팅화면 
  ```

* 화면의 큰 두 부분이 각각 독립적으로 rendering되어야한다.

### 동적 라우팅(고민진행중)

<h5 style="background-color: #f1f8ff">좀 더 생각해보기</h3> 

"좀 더 생각해보기"

✨root와 params가 아니라 해당 url의 구역에 맞는 component를 지정해서인지 url이 바뀌어도 component가 동일하다면 rendering이 이루어지지 않는다! 

=> 그래서 동적 라우팅은 안되는게 아닐까? 

=> 그것보다는 default일때, setting일때를 각각 지정을 못해서가 아닐까?

[참고] https://router.vuejs.org/kr/guide/essentials/dynamic-matching.html



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



### Named View(현재 사용중)

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

✨root와 params가 아니라 해당 url의 구역에 맞는 component를 지정해서인지 url이 바뀌어도 component가 동일하다면 rendering이 이루어지지 않는다! 
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



## 2. Vuex를 이용한 상태관리

### Modal 상태관리

> [도입하게 된 이유]
> 현재 이중 분할 화면 template을 구현 중인데, 이 경우 최하위 컴포넌트에서 클릭했을 때 전체화면에 꽉차는 형태의 모달을 구현하고 싶다. 그렇다면 Modal은 최상위 분할 이전 컴포넌트에 있어야하는데, 클릭 이벤트 emit이 계속해서 발생하는 것은 비효율적이다. 따라서 Modal의 상태를 전역에서 관리해주는 것이 좋지 않을까 하는 생각을 하게 되었다. 
> [참고]
> Dynamic Reusable Vuex : https://jeongwooahn.medium.com/%EB%AA%A8%EB%8B%AC-%EB%A0%88%EC%9D%B4%EC%96%B4%ED%8C%9D%EC%97%85-%EC%BB%B4%ED%8F%AC%EB%84%8C%ED%8A%B8-%EB%A7%8C%EB%93%A4%EA%B8%B0-with-dynamic-reusable-vuex-c99e611c6133











## 4. Template & CSS



### 스쿼클 구현(SVG)

> 카카오톡 프로필모양의 스쿼클을 구현하기위해 사용하였는데, figma의 iconify를 사용할때에도 code는 span이지만 브라우저상에서 svg로 아이콘이 그려지는 것을 확인하였다. 해당 아이콘 부분에서 모달을 띄울때 미세한 깜빡임이 발생하는 것을 파악해 이를 해결하고자 fontawesome으로 대체하였다. 그러니 깜빡임은 일어나지 않았다. 

https://velog.io/@roghabo/Squircle-%EC%8A%A4%EC%BF%BC%ED%81%B4





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





## 7. 통신

### 1. axios interceptor

[참고] https://stackoverflow.com/questions/41833424/how-to-access-vuex-module-getters-and-mutations

* axios는 Promise 기반의 HTTP Client이다. 따라서 resolve와 reject 설정이 가능한데, error가 있는 경우에는 에러를 발생시키도록 설정하기위해 `Promise.reject()`를 사용한다.
  [참고] https://redux-advanced.vlpt.us/2/02.html



```javascript
import VueAxios from "axios";
import store from "@/store";

const axios = VueAxios.create({
	// MSA 구조로 요청 주소가 각기 달라 향후 통합가능한 부분이 있으면 수정 예정
	// baseURL: 'http://....',
	// headers: {
	// 	"Content-type": "application/json",
	// },
});

axios.interceptors.request.use(
	function (config) {
		config.headers["X-ACCESS-TOKEN"] = store.getters["userStore/accessToken"];
		// config.headers["X-REFRESH-TOKEN"] = state.refreshToken;
		console.log("헤더 넣기 완료");
		console.log(store.getters["userStore/accessToken"]);
		return config;
	},
	function (error) {
		return Promise.reject(error);
	},
);

export default axios;
```



```javascript
import axios from "@/utils/axios";
```









### 3. 로그인 - access token, refresh token

> 인증(Authentication) : 로그인. 권한이 주어진 회원임을 인증 받는 것
>
> 인가(Authorization) : 인증 받은 사용자가 서비스 기능을 사용할 때 허가 받는 것
> ​                                      로그인한 사용자임을 확인함
>
> 로그인 : 데이터 베이스 저장된 사용자 계정 해시값을 가져와 사용자 암호를 복잡하게 계산해 일치하는지 확인해야함 => 계산 어려움 + 데이터베이스에서 꺼내오는 것도 자원 시간 많이 듬 / 매 요청마다 아이디 비번에 오가면 보안상 좋지 않음
>
> 세션 : 사용자가 로그인에 성공하면 세션 표딱지 출력 - 반은 사용자 브라우저 / 반은 메모리에 올려둠(데이터베이스에 넣기도 함) => 서버에 로그인이 지속되어 있는 상태
> => 단점 : 사용자가 동시 접속하면 메모리에 부하. 서버에 문제있어서 꺼지면 다 날아감
>    매 요청마다 하드에 두는건.. 서랍여닫듯 닫았다 열었다 하면... 느려짐
>   서버여러대인 경우 복잡 : 여러 서버 사이에 분산해주면 서버간에 load balancing. 세션관리해주는 서버, 기능에 사용하는 서버가 다른 경우 세션이없으므로 좋지않음 -> 공용창고나 redis와 같은 메모리형 데이터베이스에 두면.. 엎어지면 난리남.
> => 이런 부담을 없애자! JWT 등장. 
> 서버는 token만 주고 저장하지 않음.  `header. payload . verify signature` 로 세 부분으로 나뉘어짐
>
> => payload : base64 디코딩 -> json으로 누가 누구에게 발급. 언제까지 유효.  사용자에게 공개하길 원하는 내용 등이 모드 담겨 있음 = claim
> -> 일일이 서버가 뒤져볼 필요는 없음
> -> 사용자가 디코딩해서 볼수도 있는거 아닌가.. 그래서 header : type: JWT, algo : 서명값 만드는 알고지정
> 암호화 알고리즘은 한쪽에선 계산이 되지만 반대는 안됨. 서버만 알고있음 ( 서버 비밀키와 함께 알고 돌림)
>
> => 서명과 동일해야함!
>
> => 비밀값만 가지고 있음된다 - == stateless : 시간에 따라 바뀌는 상태값을 가지지 않는 것
> <-> 세션 : stateful
>
> ==> 단점 : 다중로그인. 유저관리하기엔 세션이 더 쉬움. token은 통제가 안됨. token칼취된 경우 보안 안됨.. => 만료시간을 가깝게 잡아 token수명을 짧게 준다. access token . 길게 준 refresh token 발급
>
> [참고] https://tansfil.tistory.com/59
>
> ![img](https://t1.daumcdn.net/cfile/tistory/99DB8C475B5CA1C936)
>
> => refresh token을 지워버리더라도(인가 못받게) access token이 살아있는 경우에는 바로 차단할 수는 없음
> 완전한 해결책이라고 보기는 어려움. JWT한계. 



=> 다중 로그인한 경우 : 로그인해서 새로운 access refresh를 받으니 다른 pc에서는 만료되는것? 그럼 즉시 차단은 안되겠네? 



=>

##### refresh token cookie 저장

[참고] https://velog.io/@hschoi1104/AccessToken-RefreshToken-OAuth2.0-%EA%B8%B0%EB%B0%98-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EC%9D%B8%EC%A6%9D-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-Node.js-Vue.js-1%ED%8E%B8



1안) login component에서 로그인통신 -> 토큰 저장/ vuex map actions: 친구목록가져오기(token)/router.push(친구목록)

2안) login클릭 -> map actions:로그인통신->토큰저장/ map actions 친구목록가져오기 / router.push(친구목록)

#### mutations에서 비동기처리를 하면 안 되는 이유

https://happy-coding-day.tistory.com/m/134?category=713313

#### vuex actions 비동기 처리

https://vuex.vuejs.org/kr/guide/actions.html



* 1. 로그인 새로 한 경우

     => 인증. isLogin : true => 친구목록페이지 - connection!

  2. 로그인했지만 토큰 만료된 경우

     1. rest 통신에서 access 만료신호. 재발급 API호출

  3. 다른 기기에서 로그인한 경우

     => websocket으로 

connection할때 다른기기로그인되어있는지 확인.

다른기기 로그인 되어있으면 logout

안되어있으면 login

url: / => islogin true : => friends/로 보내줌

=> islogin false: => / 유지 : 로그인 페이지



login -> connect

#### 네비게이션 가드



#### 로그인한 유저정보 저장 관리



#### 친구목록 저장 관리

=> 전역에서 사용해야하는 정보

https://minu0807.tistory.com/64



## 8.기능 구현

### 1. 로그인 - Refresh Token

> 로그인페이지에서 id password dispatch => vuex = 로그인 저장



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



### 3. 채팅방에서 메세지 수령시 최신메세지로 스크롤 이동

[참고] https://stove99.github.io/javascript/2021/04/11/scroll-to-bottom-in-vue/

* 채팅복록인 chatMessages가 변경되면 scroll이 최하단으로 이동하도록 설계

```vue
<div class="chat-messages-outer-container" id="chatMessagesContainer" ref="chatMessages">
...
<script>
chatMessages() {
			this.$nextTick(() => {
				let chatMessages = this.$refs.chatMessages;
				chatMessages.scrollTo({ top: chatMessages.scrollHeight, behavior: "smooth" });
			});
		},  
</script>
```


