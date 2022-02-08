<template>
	<div class="box">
		<div style="font-weight: bold">{{ chatInfo.roomname }}</div>
		<div>{{ chatInfo.lastMessage }}</div>
	</div>
</template>

<script>
export default {
	name: "ChatListInfo",
	props: {
		chatInfo: Object,
	},
};
</script>

<style scoped>
.box > div {
	padding-left: 10px;
	font-size: 16px;
	text-align: left;
}
</style>
