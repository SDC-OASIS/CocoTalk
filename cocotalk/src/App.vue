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
		<ProfileModal v-if="profileModal.status == 'open'" :userInfo="profileModal.userInfo" />
		<!-- <AddFriendModal /> -->
	</div>
</template>

<script>
import { mapState } from "vuex";
import Navbar from "@/components/Navbar.vue";
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
		ProfileModal,
		// AddFriendModal,
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
		...mapState("modal", ["profileModal"]),

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
.content-container {
	display: flex;
	justify-content: center;
}
.left-container {
	width: 380px;
	height: 100vh;
}
.right-container {
	width: 550px;
	height: 100vh;
}
@media (max-width: 1600px) {
	.left-container {
		width: 330px;
		height: 100vh;
	}
	.right-container {
		width: 500px;
		height: 100vh;
	}
}
.modal {
	position: fixed;
	width: 100%;
	height: 100%;
	left: 0;
	top: 0;
	background-color: rgb(0, 0, 0, 0.5);
	z-index: 1;
	justify-content: center;
	align-items: center;
}
</style>
