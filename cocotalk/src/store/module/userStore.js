import router from "@/router";
import store from "@/store";
import axios from "@/utils/axios";
import createPersistedState from "vuex-persistedstate";

const userStore = {
  plugins: [createPersistedState()],
  namespaced: true,
  state: {
    screenInfo: {
      width: Number,
    },
    accessToken: "",
    refreshToken: "",
    fcmToken: "",
    userInfo: {},
    isLogin: false,
  },
  getters: {
    accessToken(state) {
      return state.accessToken;
    },
    refreshToken(state) {
      return state.refreshToken;
    },
    userInfo(state) {
      return state.userInfo;
    },
  },
  mutations: {
    GET_SCREEN(state, payload) {
      state.screenInfo.width = payload.width;
    },
    SET_ACCESS_TOKEN(state, payload) {
      state.accessToken = payload;
    },
    CLEAR_ACCESS_TOKEN(state) {
      state.accessToken = "";
    },
    SET_REFRESH_TOKEN(state, payload) {
      state.refreshToken = payload;
    },
    CLEAR_REFRESH_TOKEN(state) {
      state.refreshToken = "";
    },
    SET_FCM_TOKEN(state, payload) {
      state.fcmToken = payload;
    },
    CLEAR_FCM_TOKEN(state) {
      state.fcmToken = "";
    },
    SET_ISLOGIN(state) {
      state.isLogin = true;
    },
    CLEAR_ISLOGIN(state) {
      state.isLogin = false;
    },
    SET_USER(state, payload) {
      state.userInfo = payload;
    },
    UPDATE_PROFILE(state, payload) {
      state.userInfo.profile.profile = payload;
    },
    UPDATE_BACKGROUND(state, payload) {
      state.userInfo.profile.background = payload;
    },
    GO_MAINPAGE() {
      router.push({ name: "friends" }).catch(() => {});
    },
    GO_LOGINPAGE() {
      router.push({ name: "login" }).catch(() => {});
    },
  },
  actions: {
    getScreen: function (context, payload) {
      context.commit("GET_SCREEN", payload);
    },
    login: async function (context, payload) {
      const userInfo = payload;
      await axios.post("auth/signin", userInfo).then((res) => {
        if (res.data.isSuccess) {
          context.commit("SET_ACCESS_TOKEN", res.data.result.accessToken);
          context.commit("SET_REFRESH_TOKEN", res.data.result.refreshToken);
          context.commit("SET_FCM_TOKEN", userInfo.fcmToken);
          context.commit("SET_ISLOGIN");
          context.commit("GO_MAINPAGE");
        } else {
          const payload = {
            status: "open",
            text: "아이디와 비밀번호를 확인해주세요.",
          };
          store.dispatch("modal/openAlert", payload, { root: true });
        }
      });
    },
    logout: async function (context) {
      context.commit("CLEAR_ACCESS_TOKEN");
      context.commit("CLEAR_REFRESH_TOKEN");
      context.commit("CLEAR_ISLOGIN");
      context.commit("GO_LOGINPAGE");
      store.dispatch("chat/clearPage");
      const headers = { action: "leave" };
      store.state.socket.stompChatListClient.disconnect(() => {}, headers);
    },
    getUser: function (context) {
      axios.get("user/profile/token").then((res) => {
        let userInfo = res.data.data;
        userInfo.profile = JSON.parse(userInfo.profile);
        context.commit("SET_USER", userInfo);
      });
    },
    reissue: ({ state, commit }) => {
      return new Promise((resolve, reject) => {
        // 현재 재발급 API 호출이 진행 중이라면 시도하지 않습니다.
        if (axios.reissueProgress) return;
        axios.reissueProgress = true;
        axios
          .get("auth/reissue", { headers: { "X-REFRESH-TOKEN": state.refreshToken } })
          .then((res) => {
            if (res.data.isSuccess) {
              commit("SET_ACCESS_TOKEN", res.data.result.accessToken);
              commit("SET_REFRESH_TOKEN", res.data.result.refreshToken);
              console.log("[VUEX] TOKEN 재설정 완료");
              resolve(res);
            } else {
              reject(res);
            }
          })
          .catch((error) => {
            console.error(error);
            reject(error);
          })
          .finally(() => {
            axios.reissueProgress = false;
          }); //axios END
      }); //Promise END
    }, //reissue END
    updateProfile: async function (context, payload) {
      var frm = new FormData();
      frm.append("profileImg", payload);
      frm.append("profileImgThumb", payload);
      await axios.put("user/profile/img", frm, { headers: { "Content-Type": "multipart/form-data" } }).then((res) => {
        console.log(JSON.parse(res.data.data.profile).profile);
        context.commit("UPDATE_PROFILE", JSON.parse(res.data.data.profile).profile);
      });
    },
    updateBG: async function (context, payload) {
      var frm = new FormData();
      frm.append("bgImg", payload);
      await axios.put("user/profile/bg", frm, { headers: { "Content-Type": "multipart/form-data" } }).then((res) => {
        console.log(res.data);
        context.commit("UPDATE_BACKGROUND", JSON.parse(res.data.data.profile).background);
      });
    },
  }, //actions END
  modules: {},
};

export default userStore;
