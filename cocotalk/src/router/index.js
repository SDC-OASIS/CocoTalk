import Vue from "vue";
import VueRouter from "vue-router";
import Login from "../views/Login.vue";
import MainPage from "../views/MainPage.vue";
import FriendList from "../views/FriendList.vue";
import ChatRoomList from "../views/ChatRoomList.vue";
import ChatRoom from "../views/ChatRoom.vue";
import BeforeEnterChat from "../views/BeforeEnterChat.vue";
import Setting from "../views/Setting.vue";
import PageNotFound from "../views/PageNotFound.vue";
import store from "../store";

Vue.use(VueRouter);

const routes = [
  {
    path: "/login",
    name: "login",
    components: {
      login: Login,
    },
  },
  {
    path: "/",
    name: "MainPage",
    component: MainPage,
    children: [
      {
        path: "/friends",
        name: "friends",
        components: {
          left: FriendList,
          right: BeforeEnterChat,
        },
        beforeEnter: (to, from, next) => {
          store.dispatch("userStore/getUser");
          store.dispatch("friend/getFriends");
          next();
        },
      },
      {
        path: "/chats",
        name: "chats",
        components: {
          left: ChatRoomList,
          right: BeforeEnterChat,
        },
      },
      {
        path: "/friends/setting",
        name: "friendsSetting",
        components: {
          left: FriendList,
          right: Setting,
        },
      },
      {
        path: "/chats/setting",
        name: "chatsSetting",
        components: {
          left: ChatRoomList,
          right: Setting,
        },
      },
      {
        path: "/friends/:chat/:roomId?",
        name: "friendsChat",
        components: {
          left: FriendList,
          right: ChatRoom,
        },
      },
      {
        path: "/chats/:chat/:roomId?",
        name: "chatsChat",
        components: {
          left: ChatRoomList,
          right: ChatRoom,
        },
      },
      {
        path: "/error",
        name: "Error",
        components: {
          error: PageNotFound,
        },
      },
    ],
    // [로그인 여부와 진입 경로에 따른 네비게이션 가드]
    beforeEnter: (to, from, next) => {
      // 로그인하지 않은 경우 로그인 페이지로 이동
      if (!store.state.userStore.isLogin) {
        next("/login");
      }
      // 로그인된 상태로 기본 url 진입하는 경우 메인페이지로 이동
      else if (to.fullPath == "/") {
        if (store.getters["chat/roomStatus"].roomId) {
          next(`/friends/chat/${store.getters["chat/roomStatus"].roomId}`);
        } else {
          next("/friends");
        }
      }
      // 로그인된 상태로 이외 url로 진입하는 경우
      else {
        next();
      }
    },
  },
  {
    path: "*",
    redirect: "/error",
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
