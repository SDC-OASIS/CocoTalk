import createPersistedState from "vuex-persistedstate";
import axios from "@/utils/axios";

const friend = {
	namespaced: true,
	plugins: [createPersistedState()],
	state: {
		friends: [
			// {
			// 	userName: "권희은",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	userName: "고병학",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	userName: "김민정",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	userName: "황종훈",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	userName: "김김김",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	userName: "박박박",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	userName: "리리리",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	userName: "황황황",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	userName: "김김김",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	userName: "박박박",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	userName: "리리리",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	userName: "황황황",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	userName: "김김김",
			// 	statusMessage: "오늘도 좋은 하루",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
			// {
			// 	userName: "박박박",
			// 	statusMessage: "햇빛이 쨍쨍",
			// },
			// {
			// 	userName: "리리리",
			// 	statusMessage: "룰루~~ 신나는 오늘~",
			// 	profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			// 	background: "https://ifh.cc/g/qKgD7C.png",
			// },
			// {
			// 	userName: "황황황",
			// 	statusMessage: "얍얍 오늘도 화이팅",
			// 	background: "https://ifh.cc/g/CgiChn.jpg",
			// },
		],
	},
	mutations: {
		GET_FRIENDS(state, payload) {
			state.friends = payload;
		},
	},
	actions: {
		getFriends: function (context) {
			axios.get("http://138.2.88.163:8000/user/friends").then((res) => {
				console.log("친구목록 가져오기");
				let friends = res.data.data;
				console.log(res);
				friends.forEach((e) => {
					e.profile = JSON.parse(e.profile);
					e.userName = e.name;
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
