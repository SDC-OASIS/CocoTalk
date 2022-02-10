import createPersistedState from "vuex-persistedstate";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
// import axios from "../../utils/axios";
// import router from "../../router";

const socket = {
	plugins: [createPersistedState()],
	namespaced: true,
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
		setStompChatListConnected(state) {
			state.stompChatListConnected = true;
		},
		setStompChatRoomConnected(state, stompChatRoomConnected) {
			state.stompChatRoomConnected = stompChatRoomConnected;
		},
	},
	actions: {
		chatListConnect: function (context) {
			const serverURL = "http://138.2.93.111:8080/stomp";
			let socket = new SockJS(serverURL);
			context.mutations.setStompChatListClient(Stomp.over(socket));
			this.stompChatListClient.connect(
				{ view: "chatList", userId: this.userInfo.id },
				(frame) => {
					// 소켓 연결 성공
					this.connected = true;
					// this.setStompChatLsitConnected(); 왜 안되는거지???
					console.log("소켓 연결 성공", frame);
					// 채팅목록 메세지 채널 subscribe
					this.stompChatListClient.subscribe(`/topic/${this.userInfo.id}/message`, (res) => {
						console.log("구독으로 받은 메시지목록의 마지막 메세지 정보 입니다.");
						console.log(res);
						// console.log(JSON.parse(res.body).message);
						// // 받은 데이터를 json으로 파싱하고 리스트에 넣어줌
						// const receivedMessage = JSON.parse(res.body).message;
						// this.chatMessages.push(receivedMessage);
						// console.log("채팅목록");
						// console.log(this.chatMessages);
						// // 새로 메세지가 들어올 경우 마지막 메세지의 BundleId 저장
						// const payload = {
						// 	nextMessageBundleId: receivedMessage.messageBundleId,
						// };
						// this.$store.dispatch("chat/updateMessageBundleId", payload, { root: true });
					});
					// 채팅목록 채팅방정보 채널 subscribe
					this.stompChatListClient.subscribe(`/topic/${this.userInfo.id}/room`, (res) => {
						console.log("구독으로 받은 업데이트된 룸정보입니다.");
						console.log(res);

						// console.log(JSON.parse(res.body).message);
						// 받은 데이터를 json으로 파싱하고 리스트에 넣어줌
						// const receivedMessage = JSON.parse(res.body).message;
						// this.chatMessages.push(receivedMessage);
						// console.log("채팅목록");
						// console.log(this.chatMessages);
						// // 새로 메세지가 들어올 경우 마지막 메세지의 BundleId 저장
						// const payload = {
						// 	nextMessageBundleId: receivedMessage.messageBundleId,
						// };
						// this.$store.dispatch("chat/updateMessageBundleId", payload, { root: true });
					});
					// 채팅목록 새로 생섣된 채팅방정보 채널 subscribe
					this.stompChatListClient.subscribe(`/topic/${this.userInfo.id}/room/new`, (res) => {
						console.log("구독으로 받은 룸정보입니다.");
						console.log(res);

						// console.log(JSON.parse(res.body).message);
						// 받은 데이터를 json으로 파싱하고 리스트에 넣어줌
						// const receivedMessage = JSON.parse(res.body).message;
						// this.chatMessages.push(receivedMessage);
						// console.log("채팅목록");
						// console.log(this.chatMessages);
						// // 새로 메세지가 들어올 경우 마지막 메세지의 BundleId 저장
						// const payload = {
						// 	nextMessageBundleId: receivedMessage.messageBundleId,
						// };
						// this.$store.dispatch("chat/updateMessageBundleId", payload, { root: true });
					});

					// 채팅방 초대 - 이전의 Join과 다름. 좀 더 생각해보기
					// const msg = {
					// 	type: 3,
					// 	roomId: this.roomStatus.roomId,
					// 	userId: this.userInfo.id,
					// 	inviteIds: [10],
					// 	messageBundleId: this.socket.recentMessageBundleId,
					// 	content: "초대메세지",
					// };
					// console.log("초대");
					// this.stompClientChat.send(`/simple/chatroom/${this.roomStatus.roomId}/message/invite`, JSON.stringify(msg), {});
				},
				(error) => {
					// 소켓 연결 실패
					console.log("소켓 연결 실패", error);
					this.connected = false;
				},
			);
			console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
		},
	},
	modules: {},
};

export default socket;
