<template>
	<div id="app">
		<router-view name="login" />
		<router-view name="error" />

		<div>
			<Navbar v-if="nav" />
			<div class="content-container">
				<router-view name="left" class="left-container" />
				<router-view name="right" class="right-container" />
			</div>
		</div>
		<Alert v-if="alert.status == 'open'" :text="alert.text" />
		<AddFriendModal v-if="addFriendModal == 'open'" />
		<MakeChatModal v-if="makeChatModal == 'open'" />
		<RoomNameModal v-if="roomNameModal.status == 'open'" />
		<ProfileModal v-if="profileModal.status == 'open'" :userProfileInfo="profileModal.userProfileInfo" />
	</div>
</template>

<script>
import { mapState } from "vuex";
import Navbar from "@/components/Navbar.vue";
import AddFriendModal from "@/components/modals/AddFriendModal.vue";
import MakeChatModal from "@/components/modals/MakeChatModal.vue";
import RoomNameModal from "@/components/modals/RoomNameModal.vue";
import ProfileModal from "@/components/modals/ProfileModal.vue";
import Alert from "@/components/modals/Alert.vue";
// import	roomNameModal from "@/components/modals	roomNameModal.vue";

export default {
	name: "App",
	data() {
		return {
			nav: true,
		};
	},
	components: {
		Navbar,
		AddFriendModal,
		MakeChatModal,
		RoomNameModal,
		ProfileModal,
		Alert,
	},
	created() {
		const width = screen.width;
		this.$store.dispatch("userStore/getScreen", { width: width });
		// 로그인 페이지에서는 navbar 안보이게 만들기
		console.log("nav" + window.location.pathname);
		// if (window.location.pathname == "/") {
		// 	this.nav = false;
		// }
		if (this.roomStatus.mainPage == "" || this.roomStatus.mainPage == "error") {
			this.nav = false;
		} else {
			this.nav = true;
			console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		// 채팅방 목록용 소켓
		// this.$store.dispatch("chat/startConnection");
	},
	computed: {
		...mapState("userStore", ["screenInfo"]),
		...mapState("chat", ["friends", "roomStatus"]),
		...mapState("modal", ["alert", "addFriendModal", "profileModal", "makeChatModal", "roomNameModal"]),
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
