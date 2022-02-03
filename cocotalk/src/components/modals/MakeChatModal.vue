<template>
	<div class="modal row" @click.self="closeMakeChatModal">
		<div class="modal-container">
			<div @click="closeMakeChatModal">
				<span class="iconify exit" data-icon="bx:bx-x"></span>
			</div>
			<div class="modal-inner-container">
				<div class="make-chat-modal-header row">
					<div>
						<span>대화상대 선택</span>
						<span style="color: #aaaaaa; padding: 0 10px">{{ selectedFriendsCnt }}</span>
					</div>
					<div @click="openRoomNameModal">
						<Button text="확인" width="60px" height="30px" />
					</div>
				</div>
				<div v-if="selectedFriends.length" class="selected-friend-container" style="width: 100%; padding: 5px 0">
					<div class="selected-friend row" v-for="(selectedFriend, idx) in selectedFriends" :key="idx">
						<div class="box">
							{{ selectedFriend.username }}
						</div>
						<i class="delete-selected-friend fas fa-times" @click="deleteSelectedFriend(selectedFriend, idx)"></i>
					</div>
				</div>
				<div>
					<div class="make-chat-modal-input row">
						<span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
						<!-- @keyup="filter" -->
						<input v-model="searchName" type="text" placeholder="ID를 입력하세요." maxlength="20" />
					</div>
					<div class="make-chat-modal-info row" style="justify-content: left">
						<div class="friend-list-container" :style="{ height: height }">
							<div class="friend-cnt">친구 - {{ searchFriendsCnt }}</div>
							<!-- {{ searchFriends }} -->
							<div class="friend-container row wrap" v-for="(friend, idx) in searchFriends" :key="idx">
								<ProfileImg :imgUrl="friend.profile.profile" width="50px" />
								<div class="friend-name">{{ friend.username }}</div>
								<input :id="'checked' + idx" v-model="selectedFriends" type="checkbox" :value="friend" />
								<label :for="'checked' + idx"> </label>
							</div>
						</div>
						<!-- <div style="dispaly: inline-block; text-align: center">
							<div style="font-weight: bold">모코코</div>
							<div>모코코는 행복해</div>
						</div> -->
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../common/ProfileImg.vue";
import Button from "../common/Button.vue";

export default {
	name: "MakeChatModal",
	components: {
		Button,
		ProfileImg,
	},
	data() {
		return {
			searchName: "",
			selectedFriends: [],
			searchFriends: [],
		};
	},

	computed: {
		...mapState("friend", ["friends"]),
		selectedFriendsCnt() {
			console.log(this.selectedFriends);
			if (this.selectedFriends.length) {
				return this.selectedFriends.length;
			} else {
				return "";
			}
		},
		searchFriendsCnt() {
			console.log(this.searchFriends);
			if (this.searchFriends.length) {
				return this.searchFriends.length;
			} else {
				return "";
			}
		},
		// 선택한 대화상대 갯수에 따라 친구목록 높이 지정
		height() {
			if (this.selectedFriendsCnt == 0) {
				return "390px";
			} else if (this.selectedFriendsCnt <= 3) {
				return "340px";
			} else {
				return "300px";
			}
		},
		// searchFriends() {
		// 	if (this.searchName) {
		// 		const arr = [];
		// 		for (let friend in this.friends) {
		// 			console.log(friend)
		// 			if (friend.username.includes(this.searchName)) {
		// 				arr.push(friend);
		// 			}
		// 		}
		// 		return arr;
		// 	} else {
		// 		return this.friends;
		// 	}
		// },
	},
	methods: {
		closeMakeChatModal() {
			this.$store.dispatch("modal/closeMakeChatModal");
		},
		deleteSelectedFriend(friend, idx) {
			console.log(friend, idx);
			this.selectedFriends.splice(idx, 1);
		},
		openRoomNameModal() {
			console.log("얍");
			this.closeMakeChatModal();
			this.$store.dispatch("modal/openRoomNameModal", "open", { root: true });
		},
		// filter() {
		// 	if (this.searchName.length) {
		// 		// let value = this.searchName.toUpperCase();
		// 		// let arr = [];
		// 		let value = { username: this.searchName.toUpperCase() };
		// 		console.log(value);

		// 		this.friends.includes(value);
		// 		// for (let friend in this.friends) {
		// 		// 	console.log("friend", friend);
		// 		// 	if (friend.username.includes(value)) {
		// 		// 		arr.push(friend);
		// 		// 	}
		// 		// }
		// 		return (this.searchFriends = this.friends.includes(value));
		// 	} else {
		// 		return (this.searchFriends = this.friends);
		// 	}
		// },
	},

	mounted() {
		this.searchFriends = this.friends;
	},
};
</script>

<style scoped>
.modal-container {
	width: 400px;
	height: 600px;
	background-color: #ffffff;
	border-radius: 10px;
	position: relative;
	word-break: break-all;
}
.exit {
	position: absolute;
	top: 0;
	right: 0;
	margin: 13px;
	font-size: 30px;
	color: #42652b;
	cursor: pointer;
}
.modal-inner-container {
	padding: 40px 20px 40px 30px;
	text-align: left;
	max-height: 60%;
}
.make-chat-modal-header {
	justify-content: space-between;
	font-size: 20px;
}
.make-chat-modal-header > div {
	color: #42652b;
	font-weight: bold;
	margin: 10px 0;
}
.selected-friend-container {
	width: 100%;
	max-height: 70px;
	overflow: auto;
}
.selected-friend-container::-webkit-scrollbar {
	background-color: #ffffff;
	width: 10px;
}
.selected-friend-container::-webkit-scrollbar-track {
	background-color: #ffffff;
}
.selected-friend-container::-webkit-scrollbar-thumb {
	background-color: #b8c8ae;
	border-radius: 10px;
	width: 5px;
}
.selected-friend {
	border: 1px solid #000000;
	display: inline-block;
	border-radius: 10px;
	margin: 0 7px 7px 0;
	padding: 3px 10px;
	justify-content: center;
	/* text-align: center; */
	align-items: center;
}
.delete-selected-friend {
	padding-left: 20px;
}
.iconify {
	font-size: 25px;
	padding-left: 10px;
}
.make-chat-modal-input {
	text-align: center;
	border-radius: 20px;
	margin: 20px 0;
	width: 95%;
	height: 35px;
	background: #d8eec0;
	align-items: center;
	justify-content: center;
}
.make-chat-modal-input > input {
	/* display: block; */
	border: none;
	border-radius: 20px;
	/* margin: 0 0 20px 0; */
	padding: 0 4%;
	width: 90%;
	/* height: 35px; */
	background: #d8eec0;
	font-size: 20px;
}
.make-chat-modal-input > input::placeholder {
	font-size: 17px;
	color: #749f58;
}
.make-chat-modal-input > input:focus {
	outline: none;
	/* outline: 2px solid #fce41e; */
}
.make-chat-modal-info {
	max-height: 380px;
	min-height: 310px;
	height: 60%;
}
.friend-list-container {
	text-align: left;
	width: 100%;
	overflow: auto;
}
.friend-list-container::-webkit-scrollbar {
	background-color: #ffffff;
	width: 10px;
}
.friend-list-container::-webkit-scrollbar-track {
	background-color: #ffffff;
}
.friend-list-container::-webkit-scrollbar-thumb {
	background-color: #b8c8ae;
	border-radius: 10px;
	width: 5px;
}
.friend-cnt {
	/* margin-bottom: 100px; */
	/* 왜 안되지! */
	font-size: 13px;
	margin: 15px 0px 5px 0px;
}
.friend-container {
	padding: 7px 0;
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

.wrap input[type="checkbox"] {
	position: absolute;
	width: 10px;
	height: 10px;
	padding: 0;
	margin: -1px;
	overflow: hidden;
	clip: rect(0, 0, 0, 0);
	border: 0;
}
.wrap input[type="checkbox"] + label {
	display: inline-block;
	position: relative;
	padding-left: 26px;
	cursor: pointer;
}
.wrap input[type="checkbox"] + label:before {
	content: "";
	position: absolute;
	top: -15px;
	width: 30px;
	height: 30px;
	text-align: center;
	background: #fff;
	border: 1px solid #ccc;
	box-sizing: border-box;
	border-radius: 20px;
} /* 보여질 부분의 스타일을 추가하면 된다. */
.wrap input[type="checkbox"]:checked + label:after {
	content: "✔";
	text-align: center;
	position: absolute;
	top: -15px;
	width: 30px;
	height: 30px;
	background-color: #fae64c;
	border-radius: 20px;
	color: #ffffff;
	font-size: 20px;
}
</style>
