<template>
	<div id="app">
		<router-view name="login" />

		<router-view></router-view>

		<!-- <router-view name="login" />
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
		<profile-modal v-if="profileModal.status == 'open'" :userProfileInfo="profileModal.userProfileInfo" /> -->
	</div>
</template>

<script>
import { mapState } from "vuex";

export default {
	name: "App",
	data() {
		return {
			nav: true,
		};
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
		// this.chatListConnect();
	},
	computed: {
		...mapState("chat", ["roomStatus"]),
		// ...mapGetters("socket", ["getStompChatListClent", "getUserId", "getAccessToken"]),
	},
};
</script>

<style>
@import "./css/common.css";
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #000000;
	background-color: #fffacd;
	width: 100vw;
	height: 100vh;
	margin: 0;
}
</style>
