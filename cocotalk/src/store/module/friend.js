import createPersistedState from "vuex-persistedstate";
import axios from "@/utils/axios";

const friend = {
	namespaced: true,
	plugins: [createPersistedState()],
	state: {
		friends: [],
	},
	mutations: {
		GET_FRIENDS(state, payload) {
			state.friends = payload;
		},
	},
	actions: {
		getFriends: function (context) {
			axios.get("http://138.2.88.163/user/friends").then((res) => {
				console.log("친구목록 가져오기");
				let friends = res.data.data;
				// 친구가 1명이라도 존재하는 경우 STRING jSON 파싱
				if (friends.length) {
					friends.forEach((e) => {
						e.profile = JSON.parse(e.profile);
						console.log("친구프로필데이터 파싱완료");
					});
				}
				context.commit("GET_FRIENDS", friends);
				console.log(friends);
			});
		},
	},
	modules: {},
};

export default friend;
