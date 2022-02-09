<template>
	<div>
		<div id="sidebar">
			<div class="sidebar-container">
				<div class="sidebar-content">
					<div class="sidebar-title">채팅방 서랍</div>
					<!-- 사진, 동영상 -->
					<div class="sidebar-item">
						<div>
							<i class="far fa-image"></i>
							<span> 사진, 동영상 </span>
						</div>
						<div class="media-img-container row">
							<img src="https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg" alt="" />
							<img src="https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg" alt="" />
							<img src="https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg" alt="" />
							<img src="https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg" alt="" />
						</div>
					</div>
					<!-- 파일 -->
					<div class="sidebar-item">
						<i class="fas fa-folder"></i>
						<span> 파일 </span>
					</div>
					<hr />
					<!-- 공지 -->
					<div class="sidebar-title">공지</div>
					<hr />
					<!-- 대화상대 -->
					<div>
						<div class="sidebar-title">대화상대</div>
						<div class="add-member row">
							<i class="far fa-plus-square" style="font-size: 50px"></i>
							<span style="padding: 0 10px"> 대화상대 초대 </span>
						</div>
						<div class="friend-container row" v-for="(friend, idx) in friends" :key="idx">
							<div @click="openProfileModal(friend)">
								<profile-img :imgUrl="friend.profile" width="50px" />
							</div>
							<div class="friend-name" :id="'check' + idx">{{ friend.userName }}</div>
						</div>
					</div>
				</div>
				<!-- footer -->
				<div class="sidebar-footer row">
					<span class="iconify" data-icon="fe:logout"></span>
					<div class="row">
						<i class="far fa-star"></i>
						<!-- <i class="fas fa-star">채워진별</i> -->
						<span class="iconify" data-icon="bi:bell-fill"></span>
					</div>
				</div>
			</div>
			<div class="sidebar-background" @click="closeSidebar"></div>
		</div>
		<div class="outer-background-container"></div>
	</div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../common/ProfileImg.vue";

export default {
	name: "Sidebar",
	components: {
		ProfileImg,
	},
	computed: {
		...mapState("chat", ["friends"]),
	},
	methods: {
		closeSidebar() {
			const sidebar = document.querySelector(".sidebar-container");
			const sidebarBack = document.querySelector(".sidebar-background");
			sidebarBack.style.display = "none";
			sidebar.style.right = "-402px";
		},
		openProfileModal(userProfileInfo) {
			this.$store.dispatch("modal/openProfileModal", { status: "open", userProfileInfo: userProfileInfo }, { root: true });
		},
	},
};
</script>

<style scoped>
.outer-background-container {
	position: absolute;
	top: 0;
	right: -400px;
	height: 100%;
	width: 400px;
	background-color: #fffacd;
	border-left: 2px solid #9eac95;
	z-index: 3;
}
.sidebar-container {
	position: absolute;
	top: 0;
	right: -400px;
	height: 100%;
	padding-bottom: 50px;
	width: 400px;
	border-left: 2px solid #9eac95;
	border-right: 2px solid #9eac95;
	background-color: #ffffff;
	z-index: 2;
	transition: 1s ease;
	text-align: left;
}
.sidebar-content {
	overflow: auto;
	height: 87%;
	padding-right: 5px;
	padding: 20px 5px 0 0;
}
.sidebar-content::-webkit-scrollbar {
	background-color: #ffffff;
	width: 18px;
}
.sidebar-content::-webkit-scrollbar-track {
	background-color: #ffffff;
	width: 10px;
}
.sidebar-content::-webkit-scrollbar-thumb {
	background-color: #b8c8ae;
	border-radius: 10px;
	width: 10px;
	background-clip: padding-box;
	border: 5px solid transparent;
}
.sidebar-background {
	position: absolute;
	top: 0;
	width: 100%;
	height: 100%;
	z-index: 1;
	background-color: rgb(0, 0, 0, 0.5);
	display: none;
	top: 0;
	width: 100%;
	height: 100%;
	z-index: 1;
	background-color: rgb(0, 0, 0, 0.5);
	display: none;
}

.sidebar-title {
	padding: 10px 20px 20px 20px;
	color: #42652b;
	font-size: 20px;
	font-weight: bold;
}
.sidebar-item {
	color: #90949b;
	padding: 10px 20px 10px 20px;
}
.sidebar-item:hover {
	background-color: #e7f7dd;
	cursor: pointer;
}
hr {
	border-color: #b8c8ae;
	margin: 10px;
}
.media-img-container > img {
	width: 65px;
	padding: 10px 10px 5px 0;
}
.add-member {
	padding: 10px 20px;
	font-size: 20px;
	color: #b4d89d;
	align-items: center;
}
.friend-container {
	padding: 7px 20px;
	align-items: center;
	cursor: pointer;
}
.friend-container img {
	width: 50px;
	height: 50px;
}
.friend-name {
	font-weight: bold;
	padding-left: 15px;
}
.sidebar-footer {
	height: 10px;
	width: 360px;
	background-color: #b4d89d;
	position: fixed;
	bottom: 0;
	justify-content: space-between;
	align-items: center;
	padding: 20px;
	font-size: 24px;
	color: #42652b;
}
.sidebar-footer > div {
	width: 70px;
	justify-content: space-between;
	align-items: center;
	font-size: 22px;
}
@media (max-width: 1600px) {
	.sidebar-container {
		width: 350px;
	}
	.sidebar-footer {
		width: 310px;
	}
}
</style>
