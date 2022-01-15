<template>
	<div class="about">
		<h1>채팅시작해주세요!!!!.</h1>
		<button @click="changeNow">kkk</button>
		{{ this.$route.params.roomId }}
		{{ roomStatus }}
	</div>
</template>

<script>
import { mapState } from "vuex";

export default {
	data() {
		return {
			now: this.$route.params.roomId,
		};
	},
	created() {
		console.log("채팅을 시작해주세요");
	},
	computed: {
		...mapState({
			roomStatus: (state) => state.roomStatus,
			friends: (state) => state.friends,
		}),
	},
	watch: {
		"$route.params.roomId": function () {
			console.log("채팅을 시작해주세요");
		},
	},
	methods: {
		changeNow() {
			this.$store.dispatch("changePage", { chat: "chat", roomId: "4" });
		},
	},
};
</script>

<style scoped>
.about {
	background-color: #e6eede;
}
</style>
