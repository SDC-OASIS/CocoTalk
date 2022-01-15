import { NULL } from "mysql/lib/protocol/constants/types";
import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
	plugins: [createPersistedState()],
	state: {
		userInfo: {
			name: "모코코",
			statusMessage: "오늘도 좋은 하루",
			profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
		},
		roomStatus: {
			chatPage: "chat",
			roomId: "111",
		},
		friends: [
			{
				name: "권희은",
				statusMessage: "오늘도 좋은 하루",
				profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
			},
			{
				name: "고병학",
				statusMessage: "햇빛이 쨍쨍",
				profile: NULL,
			},
		],
		chats: [
			{ name: "오아시스팀", lastMessage: "오늘도 좋은 하루", roomId: "111" },
			{ name: "스토브캠프", lastMessage: "햇빛이 쨍쨍", roomId: "222" },
		],
	},
	mutations: {
		CHANGE_PAGE(state, payload) {
			state.roomStatus.chatPage = payload.chat;
			state.roomStatus.roomId = payload.roomId;
		},
	},
	actions: {
		changePage: function (context, payload) {
			context.commit("CHANGE_PAGE", payload);
		},
	},
	modules: {},
});
