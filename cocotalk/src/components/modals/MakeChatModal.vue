<template>
	<div class="modal row" @click.self="closeAddFriendModal">
		<div class="modal-container">
			<div @click="closeAddFriendModal">
				<span class="iconify exit" data-icon="bx:bx-x" style="color: white"></span>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: "ProfileModal",
	methods: {
		closeAddFriendModal() {
			this.$store.dispatch("modal/closeAddFriendModal");
		},
	},
};
</script>

<style scoped>
.modal-container {
	width: 400px;
	height: 600px;
	background-color: #22291c;
	border-radius: 10px;
	position: relative;
	cursor: pointer;
}
.exit {
	position: absolute;
	top: 0;
	right: 0;
	margin: 13px;
	font-size: 30px;
	cursor: pointer;
}
</style>
