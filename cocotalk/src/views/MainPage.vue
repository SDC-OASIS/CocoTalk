<template>
	<div id="mainPage">
		<div v-if="this.stompChatListConnected">
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
	data() {
		return {
			nav: true,
		};
	},
	components: {
		Navbar,
		AddFriendModal,
		ChatCreationModal,
		RoomNameEditModal,
		ProfileModal,
		Alert,
	},

	created() {
		this.setStompChatListDisconnect();
		const width = screen.width;
		this.$store.dispatch("userStore/getScreen", { width: width });
		// 에러페이지에서는 navbar 안보이게 만들기
		console.log("nav");
		console.log(window.location.pathname);
		if (window.location.pathname == "/error") {
			this.nav = false;
		}
		// if (this.roomStatus.mainPage == "" || this.roomStatus.mainPage == "error") {
		// 	this.nav = false;
		// } else {
		// 	console.log("!!");
		// 	this.nav = true;
		// }
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
		...mapMutations("socket", ["setStompChatListClient", "setStompChatRoomClient", "setStompChatListConnected", "setStompChatListDisconnect", "setStompChatRoomConnected"]),
		chatListConnect: function () {
			const serverURL = "http://138.2.93.111:8080/stomp";
			let socket = new SockJS(serverURL);
			this.setStompChatListClient(Stomp.over(socket));
			this.stompChatListClient.connect(
				{ view: "chatList", userId: this.userInfo.id },
				(frame) => {
					// 소켓 연결 성공
					this.connected = true;
					this.setStompChatListConnected();
					console.log("소켓 연결 성공", frame);
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
