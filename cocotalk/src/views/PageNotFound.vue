<template>
	<div>
		<div class="text-container">
			<h1>
				<span>E</span>
				<span>R</span>
				<span>R</span>
				<span>O</span>
				<span>R</span>
				<span>!</span>
			</h1>
		</div>
		<div class="stage">
			<img class="jump box bounce-7" src="@/images/mococo.png" alt="mococo" />
		</div>
	</div>
</template>

<script>
import { mapState } from "vuex";

export default {
	name: "Nopage",
	data: function () {
		return {
			users: this.$store.state.users,
		};
	},
	computed: {
		...mapState("chat", ["roomStatus"]),
	},
	created() {
		console.log("에러페이지입니다.");
	},
};
</script>

<style scoped>
.jump {
	width: 201px;
	height: 260px;
	position: absolute;
	bottom: 25vh;
	left: 45vw;
	animation: bounce 0.1s ease infinite alternate;
}
/* 뛰어다니는 모션 */
.stage {
	display: flex;
	height: 330px;
}
/* 캐릭터 크기 */
.box {
	align-self: flex-end;
	animation-duration: 2s;
	animation-iteration-count: infinite;
	height: 200px;
	margin: 0 auto 0 auto;
	transform-origin: bottom;
	width: 150px;
}
.bounce-7 {
	animation-name: bounce-7;
	animation-timing-function: cubic-bezier(0.28, 0.84, 0.42, 1);
}
@keyframes bounce-7 {
	0% {
		transform: scale(1, 1) translateY(0);
	}
	10% {
		transform: scale(1.1, 0.9) translateY(0);
	}
	30% {
		transform: scale(0.9, 1.1) translateY(-100px);
	}
	50% {
		transform: scale(1.05, 0.95) translateY(0);
	}
	57% {
		transform: scale(1, 1) translateY(-7px);
	}
	64% {
		transform: scale(1, 1) translateY(0);
	}
	100% {
		transform: scale(1, 1) translateY(0);
	}
}
.text-container {
	padding-top: 25vh;
	-webkit-font-smoothing: antialiased;
	display: flex;
	justify-content: center;
	align-items: center;
}
.text-container > h1 {
	height: 100px;
}
h1 span {
	position: relative;
	top: 20px;
	padding: 0 20px;
	display: inline-block;
	animation: bounce 0.3s ease infinite alternate;
	font-size: 100px;
	color: #9eac95;
	text-shadow: 0 1px 0 #ffffff, 0 2px 0 #ffffff, 0 3px 0 #ffffff, 0 4px 0 #ffffff, 0 5px 0 #ffffff, 0 6px 0 transparent, 0 7px 0 transparent, 0 8px 0 transparent, 0 9px 0 transparent,
		0 10px 10px rgba(0, 0, 0, 0.4);
}
h1 span:nth-child(2) {
	animation-delay: 0.1s;
}
h1 span:nth-child(3) {
	animation-delay: 0.2s;
}
h1 span:nth-child(4) {
	animation-delay: 0.3s;
}
h1 span:nth-child(5) {
	animation-delay: 0.4s;
}
h1 span:nth-child(6) {
	animation-delay: 0.5s;
}
@keyframes bounce {
	100% {
		top: -20px;
		text-shadow: 0 1px 0 #ffffff, 0 2px 0 #ffffff, 0 3px 0 #ffffff, 0 4px 0 #ffffff, 0 5px 0 #ffffff, 0 6px 0 #ffffff, 0 7px 0 #ffffff, 0 8px 0 #ffffff, 0 9px 0 #ffffff, 0 50px 25px rgba(0, 0, 0, 0.2);
	}
}
</style>
© 2022 GitHub, Inc. Terms Privacy Security Status Docs Contact GitHub Pricing API Training Blog About Loading complete
