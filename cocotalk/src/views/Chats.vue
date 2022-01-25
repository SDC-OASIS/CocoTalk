<template>
	<div class="chats-container">
		<div class="header row">
			<span>채팅</span>
			<div class="header-icon-container row">
				<div style="dispaly: inline-block">
					<span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
				</div>
				<div style="dispaly: inline-block" @click="openMakeChatModal">
					<span class="iconify" data-icon="mdi:chat-plus-outline" style="color: #aaaaaa"></span>
				</div>
			</div>
		</div>
		<div class="chat-list-container">
			<div class="chat-container row" v-for="(chat, idx) in chats" :key="idx">
				<div>
					<ProfileImg :imgUrl="chat.profile" width="50px" />
				</div>
				<div class="chat-info-container row" @click="goChat(chat.roomId)">
					<ChatListInfo :chatInfo="chat" />
					<div class="box row chat-detail-info">
						<div class="received-time">오후3:00</div>
						<div class="message-cnt box">{{ chat.cnt }}</div>
					</div>
				</div>
			</div>
		</div>
		{{ roomStatus }}
	</div>
	<!-- <div>
		<h1>채팅방목록</h1>
		<div v-for="(chat, idx) in chats" :key="idx">
			<div @click="goChat(chat.roomId)">
				<div>{{ chat.name }}</div>
				<div>{{ chat.lastMessage }}</div>
			</div>
			<hr />
		</div>
	</div> -->
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../components/common/ProfileImg.vue";
import ChatListInfo from "../components/chats/ChatListInfo.vue";

export default {
	name: "ChatList",
	components: {
		ProfileImg,
		ChatListInfo,
	},
	created() {
		console.log("채팅목록");
		console.log(this.$route.params);
		this.$store.dispatch("chat/changeMainPage", "chats", { root: true });
	},
	computed: {
		...mapState("chat", ["roomStatus"]),
		...mapState("chat", ["chats"]),

		// ...mapState({
		// 	roomStatus: (state) => state.roomStatus,
		// 	chats: (state) => state.chats,
		// }),
	},
	methods: {
		openMakeChatModal() {
			// console.log("헛");
			this.$store.dispatch("modal/openMakeChatModal", "open", { root: true });
		},
		goChat(roomId) {
			this.$router.push({ name: "chatsChat", params: { chat: "chat", roomId: roomId } }).catch(() => {});
		},
	},
};
</script>
<style scoped>
.chats-container {
	display: block;
	padding-top: 20px;
	background-color: #ffffff;
	border-left: 2px solid #9eac95;
	border-right: 2px solid #9eac95;
	font-size: 15px;
	overflow: auto;
}
.header {
	justify-content: space-between;
}
.header-icon-container {
	width: 75px;
	justify-content: space-between;
	align-items: center;
	padding-right: 20px;
}
.header > span {
	color: #749f58;
	font-size: 25px;
	font-weight: bold;
	padding-left: 20px;
}
.header div {
	font-size: 23px;
	font-weight: bold;
	/* margin-right: 20px; */
}
.iconify {
	padding: 7px;
	border-radius: 15px;
}
.iconify:hover {
	background-color: #e7f7dd;
	cursor: pointer;
}

.chat-list-container {
	padding: 20px 0;
	text-align: left;
}
.chats-container::-webkit-scrollbar {
	background-color: #ffffff;
	width: 18px;
}
.chats-container::-webkit-scrollbar-track {
	background-color: #ffffff;
	width: 10px;
}
.chats-container::-webkit-scrollbar-thumb {
	background-color: #b8c8ae;
	border-radius: 10px;
	width: 10px;
	background-clip: padding-box;
	border: 5px solid transparent;
}
.chat-list-container > span {
	margin-bottom: 100px;
	/* 왜 안되지! */
}
.chat-container {
	padding: 10px 0;
	align-items: center;
	padding-left: 20px;
	cursor: pointer;
}
.chat-container img {
	width: 50px;
	height: 50px;
}
.chat-container:hover {
	background-color: #e7f7dd;
}
.chat-info-container {
	justify-content: space-between;
	width: 80%;
}
.received-time {
	color: #90949b;
	font-size: 13px;
	padding-top: 4px;
}
.message-cnt {
	background-color: #e80c4e;
	color: #ffffff;
	text-align: right;
	padding: 3px 7px;
	font-weight: bold;
	border-radius: 20px;
	margin-top: 5px;
}
.chat-detail-info {
	text-align: right;
}

@media (max-width: 1600px) {
	.chat-info-container {
		justify-content: space-between;
		width: 75%;
	}
}
</style>
