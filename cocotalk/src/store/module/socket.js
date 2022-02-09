import createPersistedState from "vuex-persistedstate";
// import Stomp from "webstomp-client";
// import SockJS from "sockjs-client";
// import axios from "../../utils/axios";
// import router from "../../router";

const chat = {
	namespaced: true,
	plugins: [createPersistedState()],
	state: {
		stompChatListClient: null,
		stompChatRoomClient: null,
		stompChatListConnected: false,
		stompChatRoomConnected: false,
	},
	// getters: {
	// 	getStompChatListClient: (state) => {
	// 		return state.stompChatListClient;
	// 	},
	// 	getStompChatRoomClient: (state) => {
	// 		return state.stompChatRoomClient;
	// 	},
	// },
	mutations: {
		setStompChatListClient(state, stompChatListClient) {
			state.stompChatListClient = stompChatListClient;
		},
		setStompChatRoomClient(state, stompChatRoomClient) {
			state.stompChatRoomClient = stompChatRoomClient;
		},
		setStompChatListConnected(state, stompChatListConnected) {
			state.stompChatListConnected = stompChatListConnected;
		},
		setStompChatRoomConnected(state, stompChatRoomConnected) {
			state.stompChatRoomConnected = stompChatRoomConnected;
		},
	},
	actions: {},
	modules: {},
};

export default chat;
