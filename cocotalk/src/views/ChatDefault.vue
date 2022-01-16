<template>
	<div class="chat-default-container row">
		<img src="../images/chat_default_mococo.png" alt="" />
		<div class="break"></div>
		<span style="">채팅을 시작해보세요.</span>
		<!-- <button @click="changeNow">kkk</button>
		{{ this.$route.params.roomId }}
		{{ roomStatus }} -->
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
.chat-default-container {
	background-color: #e6eede;
	height: 100vh;
	flex-wrap: wrap;
	justify-content: center;
	align-content: center;
}
.chat-default-container > img {
	padding-right: 50px;
}
.chat-default-container > span {
	padding-bottom: 150px;
	font-size: 25px;
	font-weight: bold;
	color: #90949b;
}
</style>
