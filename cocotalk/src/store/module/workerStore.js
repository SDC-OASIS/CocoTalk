import createPersistedState from "vuex-persistedstate";

/*
  fcm push를 위해 service worker랑 통신하기 위한 store 입니다.
*/
const workerStore = {
  plugins: [createPersistedState()],
  namespaced: true,
  state: {
    isMute: false,
  },
  getters: {
    isMute(state) {
      return state.isMute;
    },
  },
  mutations: {
    SET_IS_MUTE(state, payload) {
      state.isMute = payload;
    },
  },
  actions: {
    setMute: ({ commit }, payload) => {
      commit("SET_IS_MUTE", payload);
      const muteBroadcast = new BroadcastChannel("mute");
      muteBroadcast.postMessage({ key: payload });
    },
  },
  modules: {},
};

export default workerStore;
