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




## 2. Vuex를 이용한 상태관리

### Modal 상태관리

> [도입하게 된 이유]
> 현재 이중 분할 화면 template을 구현 중인데, 이 경우 최하위 컴포넌트에서 클릭했을 때 전체화면에 꽉차는 형태의 모달을 구현하고 싶다. 그렇다면 Modal은 최상위 분할 이전 컴포넌트에 있어야하는데, 클릭 이벤트 emit이 계속해서 발생하는 것은 비효율적이다. 따라서 Modal의 상태를 전역에서 관리해주는 것이 좋지 않을까 하는 생각을 하게 되었다. 
> [참고]
> Dynamic Reusable Vuex : https://jeongwooahn.medium.com/%EB%AA%A8%EB%8B%AC-%EB%A0%88%EC%9D%B4%EC%96%B4%ED%8C%9D%EC%97%85-%EC%BB%B4%ED%8F%AC%EB%84%8C%ED%8A%B8-%EB%A7%8C%EB%93%A4%EA%B8%B0-with-dynamic-reusable-vuex-c99e611c6133

[....추가중....]



## 3. 다중 로그인 관리

>  웹기기 1대, 모바일 기기 1대만 동시 로그인이 가능하도록 설정하였다.

* 서비스가 진행되는 동안 항상 connection을 유지하는 소켓으로 다른 기기에 로그인이 되었다는 정보를 받으면 로그아웃 되도록 설정한다. 
* 잠자기 상태였거나 PC가 통신이 이루어지지 않았던 경우 서비스 이용을 위한 요청을 보낼때 fcm Token을 보내 가장 최근 로그인한 기기가 맞는지 비교하고 다른 경우에는 로그아웃 되도록 설정한다.



## 4. Template & CSS



### 스쿼클 구현(SVG)

> 카카오톡 프로필모양의 스쿼클을 구현하기위해 사용하였는데, figma의 iconify를 사용할때에도 code는 span이지만 브라우저상에서 svg로 아이콘이 그려지는 것을 확인하였다. 해당 아이콘 부분에서 모달을 띄울때 미세한 깜빡임이 발생하는 것을 파악해 이를 해결하고자 fontawesome으로 대체하였다. 그러니 깜빡임은 일어나지 않았다. 

[참고] https://velog.io/@roghabo/Squircle-%EC%8A%A4%EC%BF%BC%ED%81%B4



### Infinite Scroll

[....현재 구현중 ...]




