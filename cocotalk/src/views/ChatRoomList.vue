<template>
	<div class="chat-room-list-outer-container">
		<div class="header row">
			<span>채팅</span>
			<div class="header-icon-container row">
				<div style="dispaly: inline-block">
					<span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
				</div>
				<div style="dispaly: inline-block" @click="openChatCreationModal">
					<span class="iconify" data-icon="mdi:chat-plus-outline" style="color: #aaaaaa"></span>
				</div>
			</div>
		</div>
		<div class="chat-room-list-container">
			<div class="chat-room-item-container row" v-for="(chat, idx) in chats" :key="idx">
				<!-- <div>
					<ProfileImg :imgUrl="chat.img" width="50px" />
				</div> -->
				<div style="dispaly: inline-block; text-align: center">
					<div v-if="chat.room.members.length == 1">
						<profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 50px" />
					</div>
					<div v-if="chat.room.members.length == 2" style="width: 50px; height: 60px">
						<div style="position: absolute">
							<div v-if="!chat.img">
								<profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width="30px" :radius="3" />
								<profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 30px" class="two-friends-second-img" :radius="3" />
							</div>
							<div v-else>
								<profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 30px" />
							</div>
						</div>
					</div>
					<div v-if="chat.room.members.length == 3" style="width: 50px; height: 50px; padding-left: 7px">
						<div style="position: absolute">
							<profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 30px" />
							<profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 30px" class="three-friends-second-img" :radius="4" />
							<profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 30px" class="three-friends-third-img" :radius="4" />
						</div>
					</div>
					<div v-if="chat.room.members.length >= 4" style="width: 50px; height: 50px; padding-top: 7px">
						<div style="position: absolute">
							<profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" class="four-friends-first-img" width=" 25px" />
							<profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 25px" class="four-friends-second-img" :radius="5" />
							<profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 25px" class="four-friends-third-img" :radius="5" />
							<profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 25px" class="four-friends-forth-img" :radius="5" />
						</div>
					</div>
				</div>
				<div class="chat-info-container row" @click="goChat(chat)">
					<chat-list-info :chatInfo="chat" />
					<div class="box row chat-detail-info">
						<div class="received-time">{{ messageSentTime(chat.recentChatMessage.sentAt) }}</div>
						<div class="message-cnt box">{{ chat.unreadNumber }}</div>
					</div>
				</div>
			</div>
		</div>
		{{ roomStatus }}
	</div>
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
		console.log("채팅목록입니다.");
		console.log(this.$route.params);
		this.$store.dispatch("chat/changeMainPage", "chats", { root: true });
		this.$store.dispatch("chat/getChatList");
	},
	computed: {
		...mapState("chat", ["roomStatus"]),
		...mapState("chat", ["chats"]),
	},
	methods: {
		openChatCreationModal() {
			this.$store.dispatch("modal/openChatCreationModal", "open", { root: true });
		},
		goChat(chat) {
			let payload = {
				roomId: chat.room.id,
				nextMessageBundleId: chat.room.messageBundleIds[chat.room.messageBundleIds.length - 1],
				recentMessageBundleCount: chat.recentMessageBundleCount,
			};
			this.$store.dispatch("chat/goChat", payload, { root: true });
		},
		messageSentTime(time) {
			return this.$moment(time).format("LT");
		},
	},
};
</script>
<style scoped>
.chat-room-list-outer-container {
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

.chat-room-list-container {
	padding: 20px 0;
	text-align: left;
}
.chat-room-list-outer-container::-webkit-scrollbar {
	background-color: #ffffff;
	width: 18px;
}
.chat-room-list-outer-container::-webkit-scrollbar-track {
	background-color: #ffffff;
	width: 10px;
}
.chat-room-list-outer-container::-webkit-scrollbar-thumb {
	background-color: #b8c8ae;
	border-radius: 10px;
	width: 10px;
	background-clip: padding-box;
	border: 5px solid transparent;
}
.chat-room-list-container > span {
	margin-bottom: 100px;
	/* 왜 안되지! */
}
.chat-room-item-container {
	padding: 10px 0;
	align-items: center;
	padding-left: 20px;
	cursor: pointer;
}
.chat-room-item-container img {
	width: 50px;
	height: 50px;
}
.chat-room-item-container:hover {
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
.two-friends-second-img {
	position: relative;
	top: -20px;
	left: -14px;
}

.three-friends-second-img {
	position: relative;
	top: -15px;
	left: -45px;
}
.three-friends-third-img {
	position: relative;
	top: -15px;
	left: -20px;
}
.four-friends-first-img {
	position: relative;
	top: -5px;
	left: -3px;
	z-index: 3;
}
.four-friends-second-img {
	position: relative;
	top: -35px;
	left: -6px;
	z-index: 3;
}
.four-friends-third-img {
	position: relative;
	top: -8px;
	left: -34px;
}
.four-friends-forth-img {
	position: relative;
	top: -8px;
	left: -7px;
}
</style>
