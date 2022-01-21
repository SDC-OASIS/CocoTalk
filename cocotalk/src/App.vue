<template>
	<div id="app">
		<router-view name="login" />
		<div>
			<Navbar v-if="nav" />
			<div class="content-container">
				<router-view name="left" class="left-container" />
				<router-view name="right" class="right-container" />
			</div>
		</div>
		<ProfileModal v-if="profileModal.status == 'open'" :userProfileInfo="profileModal.userProfileInfo" />
		<ProfileModal v-if="profileModal.status == 'open'" :userProfileInfo="profileModal.userProfileInfo" />
		<AddFriendModal v-if="addFriendModal == 'open'" />
	</div>
</template>

<script>
import { mapState } from "vuex";
import Navbar from "@/components/Navbar.vue";
import AddFriendModal from "@/components/modals/AddFriendModal.vue";
import ProfileModal from "@/components/modals/ProfileModal.vue";
// import AddFriendModal from "@/components/modals/AddFriendModal.vue";

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
		ProfileModal,
	},
	created() {
		const width = screen.width;
		this.$store.dispatch("userStore/getScreen", { width: width });
		// 로그인 페이지에서는 navbar 안보이게 만들기
		if (window.location.pathname == "/") {
			this.nav = false;
		}
	},
	computed: {
		...mapState("userStore", ["screenInfo"]),
		...mapState("chat", ["friends"]),
		...mapState("modal", ["profileModal", "addFriendModal"]),

		// ...mapState(userStore, {
		// 	screenInfo: (state) => state.roomStatus,
		// 	friends: (state) => state.friends,
		// }),
	},
	methods: {
		// showProfile() {
		// 	console.log("조회");
		// },
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
