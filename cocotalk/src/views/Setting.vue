<template>
	<div>
		<h1>설정페이지입니다.</h1>
		<button @click="check">ss</button>
	</div>
</template>

<script>
export default {
	name: "Setting",
	created() {
		console.log("설정 페이지");
	},
	methods: {
		check() {
			// console.log(window.location.pathname.split("/")[1]); = fiends같은 앞머리 가져오는 방법 연구
		},
	},
};
</script>

<style></style>
