<template>
	<div class="home">
		<img alt="Vue logo" src="../assets/logo.png" />
		<HelloWorld class="dd" msg="Welcome to Your Vue.js App" />
	</div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";

export default {
	name: "Home",
	components: {
		HelloWorld,
	},
};
</script>
<style scoped>
.dd {
	display: block;
	justify-content: center;
}
</style>
