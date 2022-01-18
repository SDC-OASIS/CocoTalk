import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";
import modules from "./module";
// import chat from "@/store/modules/chat.js";
// import userStore from "@/store/modules/userStore.js";

Vue.use(Vuex);
export default new Vuex.Store({
	modules,
	plugins: [createPersistedState()],
});
// const store = new Vuex.Store({ modules });
// export default store;

// export default new Vuex.Store({
// 	modules: {
// 		chat: chat,
// 		userStore: userStore,
// 	},
// 	plugins: [createPersistedState()],
// 	state: {},
// 	mutations: {},
// 	actions: {},
// });
