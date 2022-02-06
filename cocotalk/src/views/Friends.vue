<template>
	<div class="friends-container">
		<div class="header row">
			<span>친구</span>
			<div class="header-icon-container row">
				<div style="dispaly: inline-block">
					<span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
				</div>
				<div style="dispaly: inline-block" @click="openAddFriendModal">
					<span class="iconify" data-icon="heroicons-outline:user-add" style="color: #aaaaaa"></span>
				</div>
			</div>
		</div>
		<div class="myprofile row">
			<div @click="openProfileModal(userInfo)">
				<ProfileImg :imgUrl="userInfo.profile.profile" :width="width" />
			</div>
			<FriendListUserInfo :userInfo="userInfo" />
		</div>
		<hr />
		<div class="friend-list-container">
			<div class="friend-cnt">친구 - 200</div>
			<div class="friend-container row" v-for="(friend, idx) in friends" :key="idx">
				<div @click="openProfileModal(friend)">
					<ProfileImg :imgUrl="friend.profile.profile" width="50px" />
				</div>
				<!-- {{ friend }} -->
				<FriendListUserInfo :userInfo="friend" />
			</div>
		</div>
		{{ roomStatus }}
		<button @click="test">리프레시테스트</button>
	</div>
</template>

<script>
import axios from "../utils/axios";
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
		// console.log(this.friends);
		// console.log(this.$route.params);
		if (screen.width <= 1600) {
			this.width = "60px";
		}
		this.$store.dispatch("chat/changeMainPage", "friends", { root: true });
		this.$store.dispatch("userStore/getUser");

		// this.$store.dispatch("friend/getFriends");
	},
	mounted() {
		this.$store.dispatch("chat/changeMainPage", "friends", { root: true });
	},
	computed: {
		...mapState("chat", ["roomStatus"]),
		...mapState("friend", ["friends"]),

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
		openProfileModal(userProfileInfo) {
			this.$store.dispatch("modal/openProfileModal", { status: "open", userProfileInfo: userProfileInfo }, { root: true });
		},
		openAddFriendModal() {
			console.log("얍");
			this.$store.dispatch("modal/openAddFriendModal", "open", { root: true });
		},
		test() {
			axios
				.get("http://146.56.43.7:8080/api/user/friend")
				.then((res) => {
					console.log("리프레시");
					console.log(res);
				})
				.catch((err) => {
					console.log(err);
				});
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
	overflow: auto;
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
.header > div {
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
.friends-container::-webkit-scrollbar {
	background-color: #ffffff;
	width: 18px;
}
.friends-container::-webkit-scrollbar-track {
	background-color: #ffffff;
	width: 10px;
}
.friends-container::-webkit-scrollbar-thumb {
	background-color: #b8c8ae;
	border-radius: 10px;
	width: 10px;
	background-clip: padding-box;
	border: 5px solid transparent;
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
/* .d {
	position: absolute;
	width: 50px;
	height: 50px;
	z-index: 3000;
} */
</style>
