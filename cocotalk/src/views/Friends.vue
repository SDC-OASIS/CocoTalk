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
			<div>
				<ProfileImg :imgUrl="userInfo.profile" :width="width" />
			</div>
			<FriendListUserInfo :userInfo="userInfo" />
		</div>
		<hr />
		<div class="friend-list-container">
			<div class="friend-cnt">친구 - 200</div>
			<div class="friend-container row" v-for="(friend, idx) in friends" :key="idx">
				<div @click="openProfileModal(friend)">
					<ProfileImg :imgUrl="friend.profile" width="50px" />
				</div>
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
	data() {
		return {
			width: "60px",
		};
	},
	created() {
		console.log("친구목록");
		console.log(this.$route.params);
		if (screen.width <= 1600) {
			this.width = "60px";
		}
	},
	computed: {
		...mapState("chat", ["roomStatus"]),
		...mapState("chat", ["friends"]),
		...mapState("userStore", ["userInfo"]),
		...mapState("userStore", ["screenInfo"]),

		// ...mapState({
		// 	roomStatus: (state) => state.roomStatus,
		// 	friends: (state) => state.friends,
		// 	userInfo: (state) => state.userInfo,
		// 	screenInfo: (state) => state.screenInfo,
		// }),
	},
	methods: {
		noImage(e) {
			e.targer.src = "@/assets/profile.jpg";
		},
		openProfileModal(userInfo) {
			this.$store.dispatch("modal/openProfileModal", { status: "open", userInfo: userInfo }, { root: true });
			console.log("조회");
		},
	},
};
// var toolbar = document.getElementById("d");
// 	toolbar.onclick = function (e) {
// 		alert("Hello");
// };
</script>

<style scoped>
.friends-container {
	display: block;
	padding-top: 20px;
	background-color: #ffffff;
	border-left: 2px solid #9eac95;
	border-right: 2px solid #9eac95;
	font-size: 15px;
}
.friends-container > hr {
	border-bottom: 1px solid #9eac95;
	width: 90%;
}
.header {
	justify-content: space-between;
	padding-bottom: 10px;
}
.header-icon-container {
	width: 70px;
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
	font-size: 28px;
	font-weight: bold;
	margin-right: 30px;
}

.myprofile {
	padding: 10px 20px 10px 20px;
	/* border-bottom: 2px solid #9eac95; */
	align-items: center;
	cursor: pointer;
}

.myprofile:hover {
	background-color: #e7f7dd;
}
.myprofile > div {
	display: inline-block;
}

.friend-list-container {
	text-align: left;
}
.friend-cnt {
	/* margin-bottom: 100px; */
	/* 왜 안되지! */
	font-size: 13px;
	margin: 15px 0px 5px 20px;
}
.friend-container {
	padding: 7px 0;
	align-items: center;
	cursor: pointer;
	padding-left: 20px;
}
.friend-container:hover {
	background-color: #e7f7dd;
}
.friend-container img {
	width: 50px;
	height: 50px;
}
.d {
	position: absolute;
	width: 50px;
	height: 50px;
	z-index: 3000;
}
</style>
