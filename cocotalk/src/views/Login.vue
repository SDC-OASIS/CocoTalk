<template>
	<div class="login row">
		<div>
			<img src="../images/mococo.png" alt="" />
			<input type="text" placeholder="ID" v-model.trim="user.cid" />
			<input type="password" placeholder="PW" v-model.trim="user.password" />
			<div>
				<button class="button-login" @click="Login" @keyup.enter="Login">로그인</button>
				<!-- <button @click="loginCancel" class="button-cancel">취소</button> -->
			</div>
		</div>
	</div>
</template>

<script>
import { mapActions } from "vuex";
import router from "@/router";

export default {
	name: "login",
	data() {
		return {
			user: {
				cid: "",
				password: "",
				fcmToken: "",
			},
		};
	},
	methods: {
		...mapActions("userStore", ["login"]),

		Login() {
			this.user.cid = this.user.cid.replace(/\s/g, "");
			this.user.password = this.user.password.replace(/\s/g, "");
			this.user.fcmToken = "token";
			console.log("로그인 클릭");
			console.log(this.user);
			// this.$store.dispatch("userStore/login", this.user);
			this.login(this.user);
			router.push({ name: "friends" }).catch(() => {});
		},
	},
};
</script>

<style>
.login {
	width: 100vw;
	height: 100vh;
	background-color: #d8eec0;
	justify-content: center;
	align-items: center;
}
.login > div {
	width: 250px;
	padding-bottom: 100px;
}
.login img {
	padding-right: 50px;
	width: 60%;
}
.login input {
	display: block;
	border: none;
	border-radius: 20px;
	margin: 0 0 20px 0;
	padding: 0 10%;
	width: 80%;
	height: 35px;
	background: #ffffff;
	font-size: 20px;
	color: #749f58;
}
.login input::placeholder {
	font-size: 20px;
	color: #749f58;
}
.login input:focus {
	outline: 2px solid #fce41e;
}
.button-login {
	border: none;
	border-radius: 10px;
	font-size: 20px;
	font-weight: 600;
	height: 40px;
	cursor: pointer;
	width: 100%;
	background-color: #749f58;
	color: #ffffff;
}
.button-login:hover {
	background-color: #486932;
	border: 1px solid rgb(230, 207, 6);
}
</style>
