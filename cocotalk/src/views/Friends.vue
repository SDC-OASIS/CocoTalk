<template>
	<div class="home">
		<h1>친구목록</h1>
		<div v-for="(friend, idx) in friends" :key="idx">
			<div>
				<div>{{ friend.name }}</div>
				<div>{{ friend.statusMessage }}</div>
			</div>
		</div>
		{{ roomStatus }}
	</div>
</template>

<script>
import { mapState } from "vuex";

export default {
	name: "FriendList",
	created() {
		console.log("친구목록");
		console.log(this.$route.params);
	},
	computed: {
		...mapState({
			roomStatus: (state) => state.roomStatus,
			friends: (state) => state.friends,
		}),
	},
};
</script>

<style scoped>
.home {
	display: block;
	justify-content: center;
	background-color: aqua;
}
</style>
