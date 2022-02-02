import router from "../../router";
import store from "../../store";
import axios from "../../utils/axios";
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
	getters: {
		accessToken(state) {
			return state.accessToken;
		},
		refreshToken(state) {
			return state.refreshToken;
		},
	},
	mutations: {
		GET_SCREEN(state, payload) {
			state.screenInfo.width = payload.width;
		},
		SET_ACCESS_TOKEN(state, payload) {
			state.accessToken = payload;
			console.log("accessToken 저장");
			store.dispatch("friend/getFriends");
			store.dispatch("userStore/getUser");
			router.push({ name: "friends" }).catch(() => {});
		},
		SET_REFRESH_TOKEN(state, payload) {
			state.refreshToken = payload;
		},
		CLEAR_ACCESS_TOKEN(state) {
			state.accessToken = "";
			console.log("accessToken 삭제");
		},
		CLEAR_REFRESH_TOKEN(state) {
			state.refreshToken = "";
			console.log("refreshToken 삭제");
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
					const payload = {
						status: "open",
						text: "아이디와 비밀번호를 확인해주세요.",
					};
					store.dispatch("modal/openAlert", payload, { root: true });
					// alert("아이디 비번을 다시 확인해주세요.");
				});
		},
		logout: function (context) {
			console.log("로그아웃합니다");
			context.commit("CLEAR_ACCESS_TOKEN");
			context.commit("CLEAR_REFRESH_TOKEN");
		},
		getUser: function (context) {
			axios.get("http://138.2.88.163/user/token").then((res) => {
				console.log("유저정보 가져오기");
				console.log(res);
			});
			console.log(context);
		},
	},
	modules: {},
};

export default userStore;
