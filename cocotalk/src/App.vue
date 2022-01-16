<template>
	<div id="app">
		<Navbar />
		<div class="content-container">
			<router-view name="left" class="left-container" />
			<router-view name="right" class="right-container" />
		</div>
	</div>
</template>

<script>
import { mapState } from "vuex";

import Navbar from "@/components/Navbar.vue";
export default {
	name: "App",
	components: {
		Navbar,
	},
	created() {
		const width = screen.width;
		this.$store.dispatch("getScreen", { width: width });
	},
	computed: {
		...mapState({
			screenInfo: (state) => state.roomStatus,
			friends: (state) => state.friends,
		}),
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
	width: 650px;
	height: 100vh;
}
@media (max-width: 1600px) {
	.left-container {
		width: 330px;
		height: 100vh;
	}
	.right-container {
		width: 520px;
		height: 100vh;
	}
}
</style>
