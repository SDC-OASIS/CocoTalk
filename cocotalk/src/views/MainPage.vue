<template>
	<div id="app">
		<router-view name="login" />
		<router-view name="error" />
		<div>
			<navbar v-if="nav" />
			<div class="content-container">
				<router-view name="left" class="left-container" />
				<router-view name="right" class="right-container" />
			</div>
		</div>
		<alert v-if="alert.status == 'open'" :text="alert.text" />
		<add-friend-modal v-if="addFriendModal == 'open'" />
		<chat-creation-modal v-if="ChatCreationModal == 'open'" />
		<room-name-edit-modal v-if="roomNameEditModal.status == 'open'" />
		<profile-modal v-if="profileModal.status == 'open'" :userProfileInfo="profileModal.userProfileInfo" />
	</div>
</template>

<script>
import { mapMutations, mapState } from "vuex";
import Navbar from "@/components/Navbar.vue";
import AddFriendModal from "@/components/modals/AddFriendModal.vue";
import ChatCreationModal from "@/components/modals/ChatCreationModal.vue";
import RoomNameEditModal from "@/components/modals/RoomNameEditModal.vue";
import ProfileModal from "@/components/modals/ProfileModal.vue";
import Alert from "@/components/modals/Alert.vue";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";

export default {
	name: "MainPage",
	// data() {
	// 	return {
	// 		nav: true,
	// 	};
	// },
	components: {
		Navbar,
		AddFriendModal,
		ChatCreationModal,
		RoomNameEditModal,
		ProfileModal,
		Alert,
	},
	created() {
		const width = screen.width;
		this.$store.dispatch("userStore/getScreen", { width: width });
		// 로그인 페이지에서는 navbar 안보이게 만들기
		// if (window.location.pathname == "/") {
		// 	this.nav = false;
		// }
		if (this.roomStatus.mainPage == "" || this.roomStatus.mainPage == "error") {
			this.nav = false;
		} else {
			console.log("!!");
			this.nav = true;
		}
		// 채팅방 목록용 소켓
		// this.$store.dispatch("chat/startConnection");
		this.chatListConnect();
	},
	computed: {
		...mapState("userStore", ["screenInfo", "userInfo"]),
		...mapState("chat", ["friends", "roomStatus"]),
		...mapState("modal", ["alert", "addFriendModal", "profileModal", "ChatCreationModal", "roomNameEditModal"]),
		...mapState("socket", ["stompChatListClient", "stompChatListConnected"]),
		// ...mapGetters("socket", ["getStompChatListClent", "getUserId", "getAccessToken"]),
	},
	methods: {
		...mapMutations("socket", ["setStompChatListClient", "setStompChatRoomClient", "setStompChatLsitConnected", "setStompChatRoomConnected"]),
		chatListConnect: function () {
			console.log("tlwkr");
			const serverURL = "http://138.2.93.111:8080/stomp";
			let socket = new SockJS(serverURL);
			this.setStompChatListClient(Stomp.over(socket));
			this.stompChatListClient.connect(
				{ view: "chatList", userId: this.userInfo.id },
				(frame) => {
					// 소켓 연결 성공
					this.setStompChatLsitConnected(true, { root: true });
					// this.connected = true;
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
					this.stompClientChat.subscribe(`/topic/${this.userInfo.id}/room`, (res) => {
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
					this.stompClientChat.subscribe(`/topic/${this.userInfo.id}/room/new`, (res) => {
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
};
</script>

<style></style>
