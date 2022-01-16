<template>
	<div class="box">
		<div style="font-weight: bold">{{ userInfo.name }}</div>
		<div>{{ userInfo.statusMessage }}</div>
	</div>
</template>

<script>
export default {
	name: "FriendListUserInfo",
	props: {
		userInfo: Object,
	},
};
</script>

<style scoped>
.box > div {
	padding-left: 20px;
	font-size: 17px;
	text-align: left;
}
</style>
