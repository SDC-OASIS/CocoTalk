import createPersistedState from "vuex-persistedstate";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import store from "@/store";

const socket = {
  plugins: [createPersistedState()],
  namespaced: true,
  state: {
    stompChatListClient: null,
    stompChatRoomClient: null,
    stompChatListConnected: false,
    stompChatRoomConnected: false,
  },
  mutations: {
    setStompChatListClient(state, stompChatListClient) {
      state.stompChatListClient = stompChatListClient;
    },
    setStompChatRoomClient(state, stompChatRoomClient) {
      state.stompChatRoomClient = stompChatRoomClient;
    },
    setStompChatListConnected(state, payload) {
      state.stompChatListConnected = payload;
    },
    setStompChatRoomConnected(state, stompChatRoomConnected) {
      state.stompChatRoomConnected = stompChatRoomConnected;
    },
    setStompChatListDisconnect(state) {
      state.stompChatListConnected = false;
    },
  },
  actions: {
    chatListConnect(context) {
      const serverURL = "http://138.2.93.111:8080/stomp";
      let socket = new SockJS(serverURL);
      context.commit("setStompChatListClient", Stomp.over(socket));
      context.state.stompChatListClient.connect(
        { view: "chatList", userId: store.getters["userStore/userInfo"] },
        (frame) => {
          this.connected = true;
          context.commit("setStompChatListConnected", true);
          console.log("소켓 연결 성공", frame);
        },
        (error) => {
          console.log("소켓 연결 실패", error);
          this.connected = false;
          context.commit("setStompChatListConnected", false);
        },
      );
      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
    },
  },
  modules: {},
};

export default socket;
