<template>
	<div class="friends-container">
		<div class="header row">
			<span>친구</span>
			<div class="header-icon-container row">
				<span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
				<span class="iconify" data-icon="heroicons-outline:user-add" style="color: #aaaaaa"></span>
			</div>
		</div>
		<div class="myprofile row">
			<ProfileImg :imgUrl="userInfo.profile" width="70px" />
			<FriendListUserInfo :userInfo="userInfo" />
		</div>
		<div class="friend-list-container">
			<span>친구 - 200</span>
			<div class="friend-container row" v-for="(friend, idx) in friends" :key="idx">
				<ProfileImg :imgUrl="friend.profile" width="50px" />
				<FriendListUserInfo :userInfo="friend" />
			</div>
		</div>
		{{ roomStatus }}
	</div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../components/common/ProfileImg.vue";
import FriendListUserInfo from "../components/friends/FriendListUserInfo.vue";

export default {
	name: "FriendList",
	components: {
		ProfileImg,
		FriendListUserInfo,
	},
	created() {
		console.log("친구목록");
		console.log(this.$route.params);
	},
	computed: {
		...mapState({
			roomStatus: (state) => state.roomStatus,
			friends: (state) => state.friends,
			userInfo: (state) => state.userInfo,
		}),
	},
	methods: {
		noImage(e) {
			e.targer.src = "@/assets/profile.jpg";
		},
	},
};
</script>

<style scoped>
.friends-container {
	display: block;
	padding-left: 2%;
	padding-top: 1.5%;
	background-color: #ffffff;
	border-left: 2px solid #9eac95;
	border-right: 2px solid #9eac95;
	font-size: 15px;
}
.header {
	justify-content: space-between;
}
.header-icon-container {
	width: 70px;
	justify-content: space-between;
	align-items: center;
}
.header > span {
	color: #749f58;
	font-size: 28px;
	font-weight: bold;
}
.header div {
	font-size: 28px;
	font-weight: bold;
	margin-right: 30px;
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

.friend-list-container {
	padding: 20px 0;
	text-align: left;
}
.friend-list-container > span {
	margin-bottom: 100px;
	/* 왜 안되지! */
}
.friend-container {
	padding: 7px 0;
	align-items: center;
}
.friend-container img {
	width: 50px;
	height: 50px;
}
</style>
