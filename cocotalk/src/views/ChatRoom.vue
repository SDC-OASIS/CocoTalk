<template>
	<div class="chat" :key="componentChat">
		<!-- 채팅방 Header -->
		<div class="chat-header row align-center">
			<div class="row align-center">
				<div @click="exitChat">
					<span class="iconify exit-chat" data-icon="eva:arrow-ios-back-outline"></span>
				</div>
				<span class="bold" style="font-size: 18px">999+</span>
			</div>
			<span class="bold" style="font-size: 18px">채팅방 이름</span>
			<div class="box">
				<span>
					<span class="iconify" data-icon="ant-design:search-outlined" style="cursor: pointer; color: black; padding-right: 10px"></span>
				</span>
				<span @click="openSidebar">
					<span class="iconify" data-icon="charm:menu-hamburger" style="color: black; cursor: pointer"></span>
				</span>
			</div>
		</div>
		<!-- 채팅 대화 -->
		<div class="chat-messages-outer-container" id="chatMessagesContainer" ref="chatMessages">
			<div class="chat-messages-container">
				<div class="chat-messages" v-for="(chatMessage, idx) in chatMessages" :key="idx">
					<!-- {{ chatMessage }} -->
					<!-- 상대가 한 말 -->
					<div v-if="chatMessage.userId != userInfo.id" class="row">
						<!-- <ProfileImg :imgUrl="chatMessage.userInfo.profile" width="40px" /> -->
						<div class="chat-message">
							<!-- <div style="padding-bottom: 7px">{{ chatMessage.userInfo.userName }}</div> -->
							<div class="row">
								<div class="bubble box">{{ chatMessage.content }}</div>
								<div style="position: relative; width: 70px">
									<div class="sent-time">오후2:00</div>
								</div>
							</div>
						</div>
					</div>
					<!-- 내가 한 말 -->
					<div v-else class="row" style="justify-content: right">
						<div class="chat-message">
							<div class="row">
								<div style="position: relative; width: 55px">
									<div class="sent-time-me">오후2:00</div>
								</div>
								<div class="bubble-me box">{{ chatMessage.content }}</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- <button @click="changeNow">kkk</button> -->
		{{ roomStatus }}
		<div class="message-input-container row">
			<textarea v-model="message"></textarea>
			<div @click="send">
				<Button text="전송" width="50px" height="30px" style="margin-top: 15px; margin-left: 16px" />
			</div>
		</div>
		<Sidebar />
	</div>
</template>

<script>
import { mapMutations, mapState } from "vuex";
// import ProfileImg from "../components/common/ProfileImg.vue";
import Button from "../components/common/Button.vue";
import Sidebar from "../components/chat/Sidebar.vue";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import axios from "@/utils/axios";

export default {
	data() {
		return {
			componentChat: 0,
			roomId: this.$route.params.roomId,
			chatMessages: [],
			roomInfo: {},
			message: "",
			roomMemberIds: [],
		};
	},
	components: {
		// ProfileImg,
		Button,
		Sidebar,
	},
	created() {
		console.log("채팅나와라");
		this.$store.dispatch(
			"chat/changePage",
			{
				mainPage: this.roomStatus.mainPage,
				roomId: this.$route.params.roomId,
			},
			{ root: true },
		);
		this.chatRoomConnect();
		this.getChat();
	},
	mounted() {
		console.log("채팅연결시작");
		console.log(this.roomId);
	},
	computed: {
		...mapState("chat", ["roomStatus", "friends", "chattings", "chatInfo"]),
		...mapState("userStore", ["userInfo"]),
		...mapState("socket", ["stompChatRoomClient", "stompChatRoomConnected"]),
	},
	watch: {
		"$route.params.roomId": function () {
			console.log("채팅을 시작합니다.");
			this.stompChatRoomClient.disconnect();
			// vuex에 마지막 페이지 방문 저장
			this.$store.dispatch(
				"chat/changePage",
				{
					chat: this.$route.params.chat,
					roomId: this.$route.params.roomId,
				},
				{ root: true },
			);
			this.chatMessages = [];
			this.getChat();
			this.chatRoomConnect();
			// this.forceRerender();
			// this.$store.dispatch("chat/getChat", { roomId: this.roomStatus.roomId }, { root: true });
		},
		// 스크롤 메세지리스트 최하단으로 이동
		chatMessages() {
			this.$nextTick(() => {
				let chatMessages = this.$refs.chatMessages;
				chatMessages.scrollTo({ top: chatMessages.scrollHeight, behavior: "smooth" });
			});
		},
	},
	methods: {
		...mapMutations("socket", ["setStompChatRoomClient", "setStompChatRoomConnected"]),

		changeNow() {
			this.$store.dispatch("chat/changePage", { chat: "chat", roomId: "4" }, { root: true });
		},
		openSidebar() {
			const sidebar = document.querySelector(".sidebar-container");
			const sidebarBack = document.querySelector(".sidebar-background");
			sidebarBack.style.display = "block";
			sidebar.style.right = "0px";
		},
		exitChat() {
			console.log("채팅방 닫기");
			this.stompChatRoomClient.disconnect();
			this.$store.dispatch("chat/changePage", { mainPage: this.roomStatus.mainPage, chat: "chat", roomId: false }, { root: true });
		},
		forceRerender() {
			console.log("재로딩");
			this.componentChat += 1;
		},
		getChat() {
			console.log("채팅내역 불러오기");
			axios.get(`chat/rooms/${this.roomStatus.roomId}/tail?count=${this.chatInfo.recentMessageBundleCount}`).then((res) => {
				console.log("채팅내역 가져오기");
				let chatData = res.data.data;
				this.chatMessages = chatData.messageList;
				this.roomInfo = chatData.room;
				let members = chatData.room.members;
				members.forEach((e) => {
					this.roomMemberIds.push(e.userId);
				});
			});
		},
		chatRoomConnect: function () {
			const serverURL = "http://138.2.93.111:8080/stomp";
			let socket = new SockJS(serverURL);
			this.setStompChatRoomClient(Stomp.over(socket));
			this.stompChatRoomClient.connect(
				{ view: "chatRoom", userId: this.userInfo.id, roomId: this.roomStatus.roomId },
				(frame) => {
					// 소켓 연결 성공
					this.connected = true;
					console.log("소켓 연결 성공", frame);
					// 채팅 메세지 채널 subscribe
					this.stompChatRoomClient.subscribe(`/topic/${this.roomStatus.roomId}/message`, (res) => {
						console.log("구독으로 받은 메시지 입니다.");
						// 받은 데이터를 json으로 파싱하고 리스트에 넣어줌
						const receivedMessage = JSON.parse(res.body);
						this.chatMessages.push(receivedMessage.message);
						console.log(receivedMessage);
						// 새로 메세지가 들어올 경우 마지막 메세지의 BundleId 저장
						const payload = {
							nextMessageBundleId: receivedMessage.bundleInfo.nextMessageBundleId,
						};
						this.$store.dispatch("chat/updateMessageBundleId", payload, { root: true });
					});
					// 채팅 메세지 룸정보 업데이트 채널 subscribe
					this.stompChatRoomClient.subscribe(`/topic/${this.roomStatus.roomId}/room`, (res) => {
						console.log("구독으로 받은 채팅방 정보입니다.");
						this.roomInfo = JSON.parse(res.body);
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
					// this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/invite`, JSON.stringify(msg), {});
				},
				(error) => {
					// 소켓 연결 실패
					console.log("소켓 연결 실패", error);
					this.connected = false;
				},
			);
			console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
		},
		send() {
			console.log("Send message");
			if (this.stompChatRoomClient && this.stompChatRoomClient.connected) {
				const msg = {
					type: 0,
					content: this.message,
					roomId: this.roomInfo.roomId,
					roomType: this.roomInfo.type,
					roomname: this.roomInfo.roomname,
					userId: this.userInfo.id,
					username: this.userInfo.username,
					receiverIds: this.roomMemberIds,
					messageBundleId: this.chatInfo.nextMessageBundleId,
				};
				this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/send`, JSON.stringify(msg));
			}
		},
	},
};
</script>

<style scoped>
.chat-header {
	justify-content: space-between;
	padding: 20px;
}
/* 왜 span으로는 하위처리가 안되지 */
.chat-header .iconify {
	font-size: 25px;
	font-weight: bold;
}
.exit-chat {
	cursor: pointer;
}
.chat-messages-outer-container {
	padding: 0px 30px;
	overflow: auto;
}
.chat-messages-container {
	height: 70vh;
}
.chat-messages-outer-container::-webkit-scrollbar {
	position: absolute;
	background-color: #d8eec0;
	width: 18px;
}
.chat-messages-outer-container::-webkit-scrollbar-track {
	background-color: #d8eec0;
	width: 10px;
}
.chat-messages-outer-container::-webkit-scrollbar-thumb {
	background-color: #b8c8ae;
	border-radius: 10px;
	width: 10px;
	background-clip: padding-box;
	border: 5px solid transparent;
}
.chat-messages {
	text-align: left;
	padding: 10px 0;
	font-size: 14px;
}

.chat-message {
	padding-left: 15px;
}

.chat {
	background-color: #d8eec0;
	border-right: 2px solid #9eac95;
	position: relative;
}
.bubble {
	position: relative;
	padding: 7px 10px;
	background: #ffffff;
	/* -webkit-border-radius: 100px; */
	/* -moz-border-radius: 10px; */
	border-radius: 5px;
	margin-right: 5px;
	max-width: 300px;
	word-break: break-all;
}

.bubble:after {
	content: "";
	position: absolute;
	border-style: solid;
	border-width: 0px 20px 15px 0;
	border-color: transparent #ffffff;
	display: block;
	width: 0;
	z-index: 1;
	left: -11px;
	top: 8px;
}
.sent-time {
	position: absolute;
	bottom: 0;
	right: 0;
	width: 70px;
	height: 15px;
	font-size: 12px;
}
.sent-time-me {
	position: absolute;
	bottom: 0;
	left: 0;
	height: 15px;
	font-size: 12px;
}

.bubble-me {
	position: relative;
	padding: 7px 10px;
	background: #ffed59;
	/* -webkit-border-radius: 100px; */
	/* -moz-border-radius: 10px; */
	border-radius: 5px;
	max-width: 300px;
	word-break: break-all;
}

.bubble-me:after {
	content: "";
	position: absolute;
	border-style: solid;
	border-width: 0px 0px 15px 20px;
	border-color: transparent #ffed59;
	display: block;
	width: 0;
	z-index: 1;
	right: -11px;
	top: 8px;
}

.message-input-container {
	position: fixed;
	background-color: #ffffff;
	height: 15vh;
	bottom: 0px;
	width: inherit;
	text-align: left;
}
.message-input-container textarea {
	width: 80%;
	margin: 15px 0;
	padding: 10px 10px;
	outline: none;
	border: none;
	resize: none;
	font-size: 20px;
}
</style>
