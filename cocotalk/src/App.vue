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
	</div>
</template>

<script>
import { mapState } from "vuex";
import Navbar from "@/components/Navbar.vue";

export default {
	name: "App",
	data() {
		return {
			nav: true,
		};
	},
	components: {
		Navbar,
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

		// ...mapState(userStore, {
		// 	screenInfo: (state) => state.roomStatus,
		// 	friends: (state) => state.friends,
		// }),
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
</style>
