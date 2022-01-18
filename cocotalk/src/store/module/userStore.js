import createPersistedState from "vuex-persistedstate";

const userStore = {
	plugins: [createPersistedState()],
	namespaced: true,
	state: {
		screenInfo: {
			width: Number,
		},
		userInfo: {
			name: "권희은",
			statusMessage: "오늘도 좋은 하루",
			profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
		},
	},
	mutations: {
		GET_SCREEN(state, payload) {
			state.screenInfo.width = payload.width;
		},
	},
	actions: {
		getScreen: function (context, payload) {
			context.commit("GET_SCREEN", payload);
		},
	},
	modules: {},
};

export default userStore;
