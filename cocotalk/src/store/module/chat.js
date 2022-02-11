import createPersistedState from "vuex-persistedstate";
// import Stomp from "webstomp-client";
// import SockJS from "sockjs-client";
import axios from "../../utils/axios";
import router from "../../router";

const chat = {
	namespaced: true,
	plugins: [createPersistedState()],
	state: {
		roomStatus: {},
		chats: [],
		chatInfo: {
			nextMessageBundleId: "",
			recentMessageBundleCount: 0,
		},
	},
	mutations: {
		CHANGE_PAGE(state, payload) {
			console.log("페이지를 전환합니다.");
			// roomId가 존재하지 않는 경우 == 채팅방이 닫혀있는 경우
			if (!payload.roomId) {
				state.roomStatus.roomId = "";
				router.push({ name: payload.mainPage }).catch((err) => {
					console.log(err);
				});
			}
			// roomId가 존재하는 경우 == 채팅방이 열려있는 경우
			else {
				// state.roomStatus.chatPage = payload.chat;
				state.roomStatus.roomId = payload.roomId;
			}
		},
		GO_CHAT(state, payload) {
			state.chatInfo.recentMessageBundleCount = payload.recentMessageBundleCount;
			state.chatInfo.nextMessageBundleId = payload.nextMessageBundleId;
		},
		CHANGE_MAIN_PAGE(state, payload) {
			state.roomStatus.mainPage = payload;
		},
		SET_CHATLIST(state, payload) {
			state.chats = payload;
		},
		SET_CONNECTION(state, payload) {
			state.socket.client = payload;
		},
		UPDATE_MESSAGE_BUNDLE_ID(state, payload) {
			state.chatInfo.nextMessageBundleId = payload.nextMessageBundleId;
		},
	},
	actions: {
		// 페이지 전환
		changePage: function (context, payload) {
			context.commit("CHANGE_PAGE", payload);
		},
		changeMainPage: function (context, payload) {
			context.commit("CHANGE_MAIN_PAGE", payload);
		},
		// 채팅방 목록
		getChatList: async function (context) {
			axios.get("chat/rooms/list").then((res) => {
				console.log("채팅방목록 가져오기");
				const chatList = res.data.data;
				console.log(chatList);
				if (chatList) {
					chatList.forEach(async (e) => {
						// if (e.img == "string" || e.img == "img" || e.img == "" || e.img == null) {
						// 	await delete e["img"];
						// }
						e.room.messageBundleIds = e.room.messageBundleIds.slice(1, -1).split(", ");
						e.room.members.forEach(async (e) => {
							// if (e.profile == "string" || e.profile == "profile" || e.profile == "" || e.profile == null) {
							// 	return await (e.profile = []);
							// }
							if (e.profile) {
								return await (e.profile = JSON.parse(e.profile));
							}
						});
					});
				}
				context.commit("SET_CHATLIST", res.data.data);
				console.log(chatList);
			});
		},
		// 채팅방 내부
		async goChat(context, payload) {
			console.log("chat");
			console.log(payload);
			await context.commit("GO_CHAT", payload);
			if (context.state.roomStatus.mainPage == "friends") {
				await router.push({ name: "friendsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
			} else {
				await router.push({ name: "chatsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
			}
		},
		createChat(context, payload) {
			axios.post("chat/rooms", payload).then((res) => {
				console.log("채팅방생성");
				console.log(res);
			});
		},
		updateMessageBundleId(context, payload) {
			context.commit("UPDATE_MESSAGE_BUNDLE_ID", payload);
		},
	},
	modules: {},
};

export default chat;
