<template>
	<div class="modal row" @click.self="closeProfileModal">
		<div class="modal-container" :style="{ backgroundImage: `url(${userProfileInfo.profile.background})` }" @click.self="openFullImg(userProfileInfo.profile.background)">
			<div @click="closeProfileModal">
				<span class="iconify exit" data-icon="bx:bx-x" style="color: white"></span>
			</div>
			<div class="modal-bottom-container">
				<div class="profile-modal-info" @click.self="openFullImg(userProfileInfo.profile.background)">
					<div style="cursor: pointer; display: inline-block" @click="openFullImg(userProfileInfo.profile.profile)">
						<ProfileImg :imgUrl="userProfileInfo.profile.profile" width="70px" />
					</div>
					<br />
					<span>{{ userProfileInfo.username }}</span>
					<br />
					<span v-if="userProfileInfo.profile.message">{{ userProfileInfo.profile.message }}</span>
					<div v-else style="height: 20px; width: 10px"></div>
				</div>
				<hr />
				<div class="modal-profile-chat" style="display: inline-block; margin: 0 20px">
					<div style="display: inline-block; margin: 0 20px">
						<i class="chat fas fa-comment"></i>
						<div style="font-size: 13px">1:1 채팅</div>
					</div>
					<div v-if="userProfileInfo.username == userInfo.username" style="display: inline-block; font-size: 20px; margin: 0 20px">
						<i class="fas fa-pen" style="margin: 10px 0"></i>
						<div style="font-size: 13px">프로필 관리</div>
					</div>
				</div>
			</div>
		</div>
		<div v-if="fullImg.status" class="full-img" :style="{ backgroundImage: `url(${fullImg.img})` }">
			<div @click="closeFullImg">
				<span class="iconify exit" data-icon="bx:bx-x" style="color: white"></span>
			</div>
		</div>
	</div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../common/ProfileImg.vue";

export default {
	name: "ProfileModal",
	components: {
		ProfileImg,
	},
	data() {
		return {
			fullImg: {
				status: false,
				img: "",
			},
			profileInfo: this.userProfileInfo,
		};
	},
	props: {
		userProfileInfo: Object,
	},
	computed: {
		...mapState("userStore", ["userInfo"]),
		...mapState("modal", ["profileModal"]),
	},
	methods: {
		closeProfileModal() {
			this.$store.dispatch("modal/closeProfileModal");
		},
		openFullImg(data) {
			this.fullImg.status = true;
			this.fullImg.img = data;
		},
		closeFullImg() {
			this.fullImg.status = false;
		},
	},
	created() {
		console.log(this.profileInfo);
	},
};

// const LayerPopup = document.getElementsByClassName(".modal");
// if (LayerPopup.length === 0) {
// 	closeProfileModal();
// }
</script>

<style scoped>
.modal-container {
	width: 400px;
	height: 600px;
	background-color: #22291c;
	border-radius: 10px;
	position: relative;
	cursor: pointer;
}
.exit {
	position: absolute;
	top: 0;
	right: 0;
	margin: 13px;
	font-size: 30px;
	cursor: pointer;
}
.modal-bottom-container {
	width: 100%;
	position: absolute;
	bottom: 0;
	padding: 40px 0;
	color: #ffffff;
}
.modal-bottom-container > hr {
	border: 1px solid #ecebeb;
}
.profile-modal-info {
	padding-bottom: 5px;
}
.profile-modal-info > div {
	padding: 3px;
}
.modal-profile-chat:hover {
	cursor: auto;
}
.modal-profile-chat > div:hover {
	color: #ccc8c8;
	cursor: pointer;
}

.chat {
	font-size: 23px;
	margin: 10px;
}
.full-img {
	width: 400px;
	height: 600px;
	background-color: #22291c;
	border-radius: 10px;
	position: absolute;
}
</style>
