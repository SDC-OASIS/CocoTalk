<template>
	<div class="about">
		<h1>채팅방입니다.</h1>
		<button @click="changeNow">kkk</button>
		{{ roomStatus }}
	</div>
</template>

<script>
import { mapState } from "vuex";

export default {
	data() {
		return {};
	},
	created() {
		console.log("채팅나와라");
	},
	computed: {
		...mapState({
			roomStatus: (state) => state.roomStatus,
			friends: (state) => state.friends,
		}),
	},
	watch: {
		"$route.params.roomId": function () {
			console.log("채팅을 시작합니다.");
			// vuex에 마지막 페이지 방문 저장
			this.$store.dispatch("changePage", { chat: this.$route.params.chat, roomId: this.$route.params.roomId });
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
	background-color: rgb(221, 171, 34);
}
</style>
