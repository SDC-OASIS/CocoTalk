import createPersistedState from "vuex-persistedstate";
import axios from "@/utils/axios";
// import user from "./user.js";

const friend = {
	namespaced: true,
	plugins: [createPersistedState()],
	state: {
		friends: [
			// {
			// 	username: "권희은",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	username: "고병학",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	username: "김민정",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	username: "황종훈",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	username: "김김김",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	username: "박박박",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	username: "리리리",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	username: "황황황",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	username: "김김김",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	username: "박박박",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	username: "리리리",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	username: "황황황",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	username: "김김김",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	username: "박박박",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	username: "리리리",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	username: "황황황",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
		],
	},
	mutations: {
		// CHANGE_PAGE(state, payload) {
		// 	state.roomStatus.chatPage = payload.chat;
		// 	state.roomStatus.roomId = payload.roomId;
		// },
		GET_FRIENDS(state, payload) {
			state.friends = payload;
			// console.log(state, payload);
		},
	},
	actions: {
		// changePage: function (context, payload) {
		// 	context.commit("CHANGE_PAGE", payload);
		// },
		getFriends: function (context) {
			// const token2 = store.userStore.state.accessToken;
			// console.log(userStore.state);
			// const token = payload;
			// // const token2 = store.userStore.state.accessToken;
			// console.log(token);

			// // console.log(token2);
			// axios
			// 	.get("http://146.56.43.7:8080/api/user/friend", {
			// 		headers: {
			// 			"X-ACCESS-TOKEN": token,
			// 		},
			// 	})

			// console.log(token2);
			axios.get("http://138.2.88.163:8000/user/friends").then((res) => {
				console.log("친구목록 가져오기");
				let friends = res.data.data;
				// console.log(friends);
				friends.forEach((e) => {
					e.profile = JSON.parse(e.profile);
					e.username = e.name;
					console.log("친구프로필데이터 파싱완료");
					context.commit("GET_FRIENDS", res.data.data);
				});
				context.commit("GET_FRIENDS", res.data.data);
			});
		},
	},
	modules: {},
};

export default friend;
