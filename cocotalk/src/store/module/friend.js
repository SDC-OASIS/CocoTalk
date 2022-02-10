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
		ADD_FRIEND(state, payload) {
			state.friends.push(payload);
		},
	},
	actions: {
		getFriends: function (context) {
			axios.get("http://138.2.88.163:8000/user/friends").then((res) => {
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
		addFriend: function (context, payload) {
			console.log(payload);
			// axios.post(`http://138.2.88.163:8000/user/friends/${this.friendInfo.id}`).then((res) => {
			// 	console.log("친구추가하기");
			// 	console.log(res);
			// 	// let friendInfo = res.data.data;
			// 	// friendInfo.profile = JSON.parse(friendInfo.profile);
			// 	// this.friendInfo = friendInfo;
			// 	console.log(this.friendInfo);

			// 	// let userInfo = res.data.data;
			// 	// userInfo.profile = JSON.parse(userInfo.profile);
			// 	// context.commit("SET_USER", userInfo);
			// 	// console.log(userInfo);
			// });
			// context.commit("ADD_FRIEND", payload);
		},
	},
	modules: {},
};

export default friend;
