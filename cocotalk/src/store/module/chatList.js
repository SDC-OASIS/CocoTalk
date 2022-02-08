import createPersistedState from "vuex-persistedstate";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import axios from "../../utils/axios";
import router from "../../router";

const chatList = {
	namespaced: true,
	plugins: [createPersistedState()],
	state: {
		// socket: {
		// 	client: Object,
		// 	recentMessageBundleId: "",
		// },
		chats: [],
		// chatInfo: {
		// 	nextMessageBundleId: "",
		// 	recentMessageBundelCount: 0,
		// },
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
		changePage: function (context, payload) {
			context.commit("CHANGE_PAGE", payload);
		},
		changeMainPage: function (context, payload) {
			context.commit("CHANGE_MAIN_PAGE", payload);
		},
		getChatList: function (context) {
			axios.get("http://138.2.88.163:8000/chat/rooms").then((res) => {
				// axios.get("http://138.2.68.7:8080/rooms/list").then((res) => {
				console.log("채팅방목록 가져오기");
				let chatList = res.data.data;
				console.log(res);
				chatList.forEach((e) => {
					if (e.img == "string" || e.img == "img" || e.img == "" || e.img == null) {
						delete e["img"];
					}
					e.roomname = e.name;
					e.messageBundleIds = e.messageBundleIds.slice(1, -1).split(", ");
				});
				context.commit("SET_CHATLIST", res.data.data);
			});
			// .catch((err) => console.log(err));
		},
		startConnection: function (context) {
			const serverURL = "http://138.2.88.163:8000/chat/stomp";
			let socket = new SockJS(serverURL);
			this.stompClient = Stomp.over(socket);
			this.stompClient.connect(
				{},
				(frame) => {
					// 소켓 연결 성공
					// this.connected = true;
					console.log("소켓 연결 성공", frame);
					// 서버의 메시지 전송 endpoint를 구독
					// 이런형태를 pub sub 구조라고 함
					// this.stompClient.subscribe("/sub/chat/room/" + this.roomId, (res) => {
					// 	console.log("구독으로 받은 메시지 입니다.", res.body);
					// 	// 받은 데이터를 json으로 파싱하고 리스트에 넣어줌
					// 	this.recvList.push(JSON.parse(res.body));
					// });
					// const msg = {
					// 	type: "JOIN",
					// 	roomId: this.roomId,
					// 	sender: this.userName,
					// };
					// this.stompClient.send("/pub/chat/message", JSON.stringify(msg), {});
				},
				(error) => {
					// 소켓 연결 실패
					console.log("소켓 연결 실패", error);
					// this.connected = false;
				},
			);
			context.commit("SET_CONNECTION", this.stompClient);
			console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
		},
		startChatConnection: function (context, payload) {
			const serverURL = "http://138.2.88.163:8000/chat/stomp";
			let socket = new SockJS(serverURL);
			let stompClientChat = Stomp.over(socket);
			stompClientChat.connect(
				{},
				(frame) => {
					// 소켓 연결 성공
					// this.connected = true;
					console.log("소켓 연결 성공", frame);
					// 서버의 메시지 전송 endpoint를 구독
					// 이런형태를 pub sub 구조라고 함
					stompClientChat.subscribe(`/topic/${payload}`, (res) => {
						console.log("구독으로 받은 메시지 입니다.", res.body);
						console.log(res);
						// 받은 데이터를 json으로 파싱하고 리스트에 넣어줌
						// this.recvList.push(JSON.parse(res.body));
					});
					const msg = {
						type: "JOIN",
						roomId: this.roomId,
						sender: this.userName,
					};
					stompClientChat.send(`/chat/${payload}/send`, JSON.stringify(msg), {});
				},
				(error) => {
					// 소켓 연결 실패
					console.log("소켓 연결 실패", error);
					// this.connected = false;
				},
			);
			context.commit("SET_CONNECTION", this.stompClient);
			console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
		},
		// sendMessage(e) {
		// 	console.log(e);
		// 	if (e.keyCode === 13 && this.userName !== "" && this.message !== "") {
		// 		this.send();
		// 		this.message = "";
		// 	}
		// },
		send() {
			console.log("Send message");
			console.log(this.message);
			if (this.stompClient && this.stompClient.connected) {
				const msg = {
					type: "TALK",
					roomId: this.roomId,
					sender: this.userName,
					message: this.message,
				};
				this.stompClient.send("/pub/chat/message", JSON.stringify(msg), (res) => {
					console.log("메세지 보내기", res);
				});
			}
		},
		getChat(context, payload) {
			console.log("chat");
			console.log(payload);
			// const url = `http://138.2.88.163:8000/chat/messages?roomid=${payload.roomId}&bundleid=${payload.nextMessageBundleId}&count=0&size=10`;
			// axios.get(url).then((res) => {
			// 	console.log(res);
			// });
			context.commit("GET_CHAT", payload);
			// context.commit("CHANGE_PAGE", {
			// 	roomId: payload.roomId,
			// });
			// console.log(context.state.roomStatus);
			if (context.state.roomStatus.mainPage == "friends") {
				router.push({ name: "friendsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
			} else {
				console.log("확인");
				router.push({ name: "chatsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
			}
			// router.push({ name: "chatsChat", params: { chat: "chat", roomId: payload.roomId } }).catch(() => {});
		},

		createChat(context, payload) {
			console.log("chatCreate");
			console.log(payload);
			axios.post("http://138.2.88.163:8000/chat/rooms", payload).then((res) => {
				console.log("채팅방생성!!!!");
				console.log(res);
				// let chatList = res.data.data;
				// chatList.forEach((e) => {
				// 	if (e.img == "string") {
				// 		delete e["img"];
				// 	}
				// });
				// context.commit("SET_CHATLIST", res.data.data);
			});
		},
		updateMessageBundleId(context, payload) {
			context.commit("UPDATE_MESSAGE_BUNDLE_ID", payload);
		},
	},
	modules: {},
};

export default chatList;
