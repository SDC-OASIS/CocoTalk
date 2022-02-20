import createPersistedState from "vuex-persistedstate";
import router from "../../router";
import axios from "@/utils/axios";
import store from "@/store";

const chat = {
  namespaced: true,
  plugins: [createPersistedState()],
  state: {
    roomStatus: {
      chatPage: "chat",
    },
    chatInfo: {
      nextMessageBundleId: "",
      recentMessageBundleCount: 0,
    },
    newRoomInfo: {},
  },
  getters: {
    roomStatus(state) {
      return state.roomStatus;
    },
    chatInfo(state) {
      return state.chatInfo;
    },
  },
  mutations: {
    CHANGE_PAGE(state, payload) {
      console.log("페이지를 전환합니다.");
      // roomId가 존재하지 않는 경우 == 채팅방이 닫혀있는 경우
      if (!payload.roomId) {
        state.roomStatus.roomId = "";
        router.push({ name: payload.mainPage });
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
    DELETE_ROOMID(state, payload) {
      state.roomStatus.mainPage = payload;
      const data = {
        mainPage: payload.mainPage,
        chatPage: "chat",
        roomId: "",
      };
      state.roomStatus = data;
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
    NEW_ROOM_INFO(state, payload) {
      state.newRoomInfo = payload;
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
    deleteRoomId: function (context, payload) {
      context.commit("DELETE_ROOMID", payload);
    },
    // 채팅방목록에서 채팅방 클릭해 채팅방 열기
    async goChat(context, payload) {
      await context.commit("GO_CHAT", payload);
      if (context.state.roomStatus.mainPage == "friends") {
        await router.push({ name: "friendsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
      } else {
        await router.push({ name: "chatsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
      }
    },
    updateMessageBundleId(context, payload) {
      context.commit("UPDATE_MESSAGE_BUNDLE_ID", payload);
    },
    updateMessageBundleCount(context, recentMessageBundleCount) {
      context.commit("UPDATE_MESSAGE_BUNDLE_COUNT", recentMessageBundleCount);
    },
    // 새로 생성한 방으로 이동
    goNewChat(context, newRoomInfo) {
      context.commit("GO_CHAT", newRoomInfo);
      context.commit("NEW_ROOM_INFO", newRoomInfo.newRoom);
      router.push({ name: store.getters["chat/roomStatus"].mainPage + "Chat", params: { chat: "chat", roomId: newRoomInfo.roomId } }).catch(() => {});
    },
    updateFile: async function (context, payload) {
      var frm = new FormData();
      frm.append("messageFile", payload.chatFile);
      frm.append("messageFileThumb", payload.chatFileThumb);
      frm.append("roomId", payload.roomId);
      return await axios.post("chat/messages/file", frm, { headers: { "Content-Type": "multipart/form-data" } });
    },
  },
  modules: {},
};

export default chat;
