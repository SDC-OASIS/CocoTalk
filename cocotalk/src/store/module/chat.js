import createPersistedState from "vuex-persistedstate";
// import axios from "../../utils/axios";
import router from "../../router";

const chat = {
  namespaced: true,
  plugins: [createPersistedState()],
  state: {
    roomStatus: {
      chatPage: "chat",
    },
    chats: [],
    chatInfo: {
      nextMessageBundleId: "",
      recentMessageBundleCount: 0,
    },
  },
  getters: {
    roomStatus(state) {
      return state.roomStatus;
    },
  },
  mutations: {
    CHANGE_PAGE(state, payload) {
      console.log("페이지를 전환합니다.");
      // roomId가 존재하지 않는 경우 == 채팅방이 닫혀있는 경우
      if (!payload.roomId) {
        state.roomStatus.roomId = "";
        router.push({ name: payload.mainPage }).catch((err) => {
          console.log(err);
        });
      }
      // roomId가 존재하는 경우 == 채팅방이 열려있는 경우
      else {
        const data = {
          mainPage: payload.mainPage,
          chatPage: "chat",
          roomId: payload.roomId,
        };
        state.roomStatus = data;
      }
    },
    CLEAR_PAGE(state) {
      state.roomStatus = { chatPage: "chat" };
    },
    GO_CHAT(state, payload) {
      state.chatInfo.recentMessageBundleCount = payload.recentMessageBundleCount;
      state.chatInfo.nextMessageBundleId = payload.nextMessageBundleId;
    },
    CHANGE_MAIN_PAGE(state, payload) {
      state.roomStatus.mainPage = payload;
    },
    SET_CHATLIST(state, payload) {
      state.chats = payload;
    },
    SET_CONNECTION(state, payload) {
      state.socket.client = payload;
    },
    UPDATE_MESSAGE_BUNDLE_ID(state, payload) {
      state.chatInfo.nextMessageBundleId = payload.nextMessageBundleId;
    },
    UPDATE_MESSAGE_BUNDLE_COUNT(state, recentMessageBundleCount) {
      state.chatInfo.recentMessageBundleCount = recentMessageBundleCount;
    },
  },
  actions: {
    // 페이지 전환
    changePage: function (context, payload) {
      context.commit("CHANGE_PAGE", payload);
    },
    changeMainPage: function (context, payload) {
      context.commit("CHANGE_MAIN_PAGE", payload);
    },
    clearPage: function (context) {
      context.commit("CLEAR_PAGE");
    },
    // 채팅방목록에서 채팅방 클릭해 채팅방 열기
    async goChat(context, payload) {
      console.log("채팅시작");
      console.log(payload);
      await context.commit("GO_CHAT", payload);
      if (context.state.roomStatus.mainPage == "friends") {
        await router.push({ name: "friendsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
      } else {
        await router.push({ name: "chatsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
      }
    },
    // createChat(context, payload) {
    //   axios.post("chat/rooms", payload).then((res) => {
    //     console.log("채팅방생성");
    //     console.log(res);
    //   });
    // },
    updateMessageBundleId(context, payload) {
      context.commit("UPDATE_MESSAGE_BUNDLE_ID", payload);
    },
    updateMessageBundleCount(context, recentMessageBundleCount) {
      context.commit("UPDATE_MESSAGE_BUNDLE_COUNT", recentMessageBundleCount);
    },
  },
  modules: {},
};

export default chat;
