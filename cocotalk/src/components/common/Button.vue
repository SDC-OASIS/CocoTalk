<template>
	<div class="button row" :style="{ width: width, height: height }">
		{{ text }}
	</div>
</template>

<script>
export default {
	name: "Button",
	props: {
		width: {
			type: String,
			default: "40px",
		},
		height: {
			type: String,
			default: "20px",
		},
		text: String,
	},
};
</script>

<style>
.button {
	background-color: #fce41e;
	border: 1px solid rgb(230, 207, 6);
	justify-content: center;
	align-items: center;
	padding: 0px 3px;
}
.button:hover {
	background-color: #c9b821;
	border: 1px solid rgb(230, 207, 6);
	cursor: pointer;
}
</style>
