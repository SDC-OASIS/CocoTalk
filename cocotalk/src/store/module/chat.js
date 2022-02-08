import createPersistedState from "vuex-persistedstate";
// import Stomp from "webstomp-client";
// import SockJS from "sockjs-client";
import axios from "../../utils/axios";
import router from "../../router";

const chat = {
	namespaced: true,
	plugins: [createPersistedState()],
	state: {
		socket: {
			client: Object,
			recentMessageBundleId: "",
		},
		roomStatus: {
			mainPage: "",
			chatPage: "chat",
			roomId: "111",
		},
		chats: [],
		chatInfo: {
			nextMessageBundleId: "",
			recentMessageBundelCount: 0,
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
		GET_CHAT(state, payload) {
			state.socket.recentMessageBundleId = payload.nextMessageBundleId;
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
			state.socket.recentMessageBundleId = payload.nextMessageBundleId;
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
			const res = await axios.get("http://138.2.88.163:8000/chat/rooms");
			console.log("채팅방목록 가져오기");
			let chatList = res.data.data;
			chatList.forEach(async (e) => {
				if (e.img == "string" || e.img == "img" || e.img == "" || e.img == null) {
					await delete e["img"];
				}
				e.messageBundleIds = e.messageBundleIds.slice(1, -1).split(", ");
				e.members.forEach(async (e) => {
					if (e.profile == "string" || e.profile == "profile" || e.profile == "" || e.profile == null) {
						return await (e.profile = []);
					}
					if (e.profile) {
						return await (e.profile = JSON.parse(e.profile));
					}
				});
			});
			context.commit("SET_CHATLIST", res.data.data);
			console.log(chatList);
		},
		// 채팅방 내부
		getChat(context, payload) {
			console.log("chat");
			console.log(payload);
			context.commit("GET_CHAT", payload);
			if (context.state.roomStatus.mainPage == "friends") {
				router.push({ name: "friendsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
			} else {
				router.push({ name: "chatsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
			}
		},
		createChat(context, payload) {
			axios.post("http://138.2.88.163:8000/chat/rooms", payload).then((res) => {
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
