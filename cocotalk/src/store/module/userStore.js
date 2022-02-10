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
		accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ7XCJ1c2VySWRcIjogMTB9IiwiaWF0IjoxNTE2MjM5MDIyfQ.nsVZnx3lFAA52CdJmH5qei0D830M3FUmoUjFBbLrZUM",
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
	},
	mutations: {
		GET_SCREEN(state, payload) {
			state.screenInfo.width = payload.width;
		},
		SET_ACCESS_TOKEN(state, payload) {
			state.accessToken = payload;
			console.log("accessToken 저장");
			// await store.dispatch("friend/getFriends");
			// await store.dispatch("userStore/getUser");
			// router.push({ name: "friends" }).catch(() => {});
		},
		CLEAR_ACCESS_TOKEN(state) {
			state.accessToken = "";
			console.log("accessToken 삭제");
		},
		SET_REFRESH_TOKEN(state, payload) {
			state.refreshToken = payload;
		},
		CLEAR_REFRESH_TOKEN(state) {
			state.refreshToken = "";
			console.log("refreshToken 삭제");
		},
		SET_FCM_TOKEN(state, payload) {
			state.fcmToken = payload;
		},
		CLEAR_FCM_TOKEN(state) {
			state.refreshToken = "";
			console.log("refreshToken 삭제");
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
			console.log(userInfo);
			await axios.post("http://138.2.88.163/auth/signin", userInfo).then((res) => {
				console.log(res.data);
				if (res.data.isSuccess) {
					console.log("로그인 요청");
					console.log(res);
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
		logout: function (context) {
			console.log("로그아웃합니다");
			context.commit("CLEAR_ACCESS_TOKEN");
			context.commit("CLEAR_REFRESH_TOKEN");
			context.commit("CLEAR_ISLOGIN");
			context.commit("GO_LOGINPAGE");
		},
		getUser: function (context) {
			axios.get("http://138.2.88.163/user/token").then((res) => {
				console.log("유저정보 가져오기");
				console.log(res);
				let userInfo = res.data.data;
				userInfo.profile = JSON.parse(userInfo.profile);
				context.commit("SET_USER", userInfo);
				console.log(userInfo);
			});
		},
	},
	modules: {},
};

export default userStore;
