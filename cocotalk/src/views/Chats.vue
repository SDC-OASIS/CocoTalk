<template>
	<div>
		<h1>채팅방목록</h1>
		<div v-for="(chat, idx) in chats" :key="idx">
			<div @click="goChat(chat.roomId)">
				<div>{{ chat.name }}</div>
				<div>{{ chat.lastMessage }}</div>
			</div>
			<hr />
		</div>
	</div>
</template>

<script>
import { mapState } from "vuex";

export default {
	name: "ChatList",
	created() {
		console.log("채팅목록");
		console.log(this.$route.params);
	},
	computed: {
		...mapState({
			roomStatus: (state) => state.roomStatus,
			chats: (state) => state.chats,
		}),
	},
	methods: {
		goChat(roomId) {
			this.$router.push({ name: "chatsChat", params: { chat: "chat", roomId: roomId } }).catch(() => {});
		},
	},
};
</script>
