import router from "../../router";
import store from "../../store";
import axios from "axios";
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
		userInfo: {
			username: "권희은",
			statusMessage: "오늘도 좋은 하루",
			profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
		},
	},
	mutations: {
		GET_SCREEN(state, payload) {
			state.screenInfo.width = payload.width;
		},
		SET_ACCESS_TOKEN(state, payload) {
			state.accessToken = payload;
			console.log("accessToken 저장");
			store.dispatch("friend/getFriends", state.accessToken);
			router.push({ name: "friends" }).catch(() => {});
		},
		SET_REFRESH_TOKEN(state, payload) {
			state.refreshToken = payload;
		},
	},
	actions: {
		getScreen: function (context, payload) {
			context.commit("GET_SCREEN", payload);
		},
		login: function (context, payload) {
			const userInfo = payload;
			axios
				.post("http://146.56.152.87:8080/api/auth/signin", userInfo)
				.then((res) => {
					console.log("로그인 요청");
					context.commit("SET_ACCESS_TOKEN", res.data.result.accessToken);
					context.commit("SET_REFRESH_TOKEN", res.data.result.refreshToken);
				})
				.catch((err) => {
					console.log(err);
					console.log("뭔가틀림");
					alert("아이디 비번을 다시 확인해주세요.");
				});
		},
	},
	modules: {},
};

export default userStore;
