<template>
	<div class="chats-container">
		<div class="header row">
			<span>채팅</span>
			<div class="header-icon-container row">
				<span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
				<!-- <span class="iconify" data-icon="heroicons-outline:user-add" style="color: #aaaaaa"></span> -->
				<span class="iconify" data-icon="mdi:chat-plus-outline" style="color: #aaaaaa"></span>
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
}
.header {
	justify-content: space-between;
}
.header-icon-container {
	width: 75px;
	justify-content: space-between;
	align-items: center;
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
	margin-right: 20px;
}
.iconify {
	padding: 7px;
	border-radius: 15px;
}
.iconify:hover {
	background-color: #e7f7dd;
	cursor: pointer;
}
.myprofile {
	padding: 20px 0;
	margin-right: 30px;
	border-bottom: 2px solid #9eac95;
	align-items: center;
}

.myprofile > div {
	display: inline-block;
}

.chat-list-container {
	padding: 20px 0;
	text-align: left;
}
.chat-list-container > span {
	margin-bottom: 100px;
	/* 왜 안되지! */
}
.chat-container {
	padding: 7px 0;
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
