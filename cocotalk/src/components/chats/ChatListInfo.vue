<template>
	<div class="box">
		<div style="font-weight: bold">{{ chatInfo.name }}</div>
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
	font-size: 17px;
	text-align: left;
}
</style>
