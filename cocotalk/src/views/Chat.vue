<template>
	<div class="chat">
		<!-- 채팅방 Header -->
		<div class="chat-header row align-center">
			<div class="row align-center">
				<span class="iconify" data-icon="eva:arrow-ios-back-outline"></span>
				<span class="bold" style="font-size: 18px">999+</span>
			</div>
			<span class="bold" style="font-size: 22px">채팅방 이름</span>
			<div class="box">
				<span class="iconify" data-icon="ant-design:search-outlined" style="color: black; padding-right: 10px"></span>
				<span class="iconify" data-icon="charm:menu-hamburger" style="color: black"></span>
			</div>
		</div>
		<!-- 채팅 대화 -->
		<div class="chat-messages-container">
			<div class="chat-messages" v-for="(chatting, idx) in chattings" :key="idx">
				<!-- 상대가 한 말 -->
				<div v-if="chatting.userInfo.name != userInfo.name" class="row">
					<ProfileImg :imgUrl="chatting.userInfo.profile" width="50px" />
					<div class="chat-message">
						<div style="padding-bottom: 7px">{{ chatting.userInfo.name }}</div>
						<div class="bubble box">{{ chatting.message }}</div>
					</div>
				</div>
				<!-- 내가 한 말 -->
				<div v-else class="row" style="justify-content: right">
					<div class="chat-message">
						<div class="bubble-me box">{{ chatting.message }}</div>
					</div>
				</div>
			</div>
		</div>
		<button @click="changeNow">kkk</button>
		{{ roomStatus }}
		<div class="message-input-container row">
			<textarea></textarea>
			<Button text="전송" width="50px" height="30px" style="margin-top: 15px; margin-left: 16px" />
		</div>
	</div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../components/common/ProfileImg.vue";
import Button from "../components/common/Button.vue";

export default {
	data() {
		return {};
	},
	components: {
		ProfileImg,
		Button,
	},
	created() {
		console.log("채팅나와라");
	},
	computed: {
		...mapState({
			userInfo: (state) => state.userInfo,
			roomStatus: (state) => state.roomStatus,
			friends: (state) => state.friends,
			chattings: (state) => state.chattings,
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
.chat-header {
	justify-content: space-between;
	padding: 20px;
}
/* 왜 span으로는 하위처리가 안되지 */
.chat-header .iconify {
	font-size: 35px;
	font-weight: bold;
}
.chat-messages-container {
	padding: 10px 20px;
}
.chat-messages {
	text-align: left;
	padding: 10px 0;
}

.chat-message {
	padding-left: 15px;
}

.chat {
	background-color: #d8eec0;
	border-right: 2px solid #9eac95;
}
.bubble {
	position: relative;
	padding: 7px 10px;
	background: #ffffff;
	/* -webkit-border-radius: 100px; */
	/* -moz-border-radius: 10px; */
	border-radius: 5px;
}

.bubble:after {
	content: "";
	position: absolute;
	border-style: solid;
	border-width: 0px 20px 15px 0;
	border-color: transparent #ffffff;
	display: block;
	width: 0;
	z-index: 1;
	left: -11px;
	top: 8px;
}

.bubble-me {
	position: relative;
	padding: 7px 10px;
	background: #ffed59;
	/* -webkit-border-radius: 100px; */
	/* -moz-border-radius: 10px; */
	border-radius: 5px;
}

.bubble-me:after {
	content: "";
	position: absolute;
	border-style: solid;
	border-width: 0px 0px 15px 20px;
	border-color: transparent #ffed59;
	display: block;
	width: 0;
	z-index: 1;
	right: -11px;
	top: 8px;
}

.message-input-container {
	position: fixed;
	background-color: #ffffff;
	height: 15vh;
	bottom: 0px;
	width: inherit;
	text-align: left;
}
.message-input-container textarea {
	width: 80%;
	margin: 15px 0;
	padding: 10px 10px;
	outline: none;
	border: none;
	resize: none;
	font-size: 20px;
}
</style>
