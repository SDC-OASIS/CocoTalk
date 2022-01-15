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
import Navbar from "@/components/Navbar.vue";
export default {
	name: "App",
	components: {
		Navbar,
	},
};
</script>

<style>
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #2c3e50;
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
	width: 400px;
	height: 100vh;
}
.right-container {
	width: 650px;
	height: 100vh;
}
</style>
