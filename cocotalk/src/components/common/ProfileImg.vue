<template>
	<div class="profile-img-container">
		<!-- <img :src="imgUrl" :style="{ width: width }" @error="$event.target.src = 'https://ifh.cc/g/pLtMj2.png'" /> -->
		<svg :width="width" :height="width" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
			<defs>
				<path
					id="shapeSquircle"
					d="M50 25C50 43.4095 43.4095 50 25 50C6.59051 50 0 43.4095 0 25C0 6.59051 6.59051 0 25 0C43.4095 0 50 6.59051 50 25Z"
				></path>
				<clipPath id="clipSquircle">
					<use xlink:href="#shapeSquircle" />
				</clipPath>
			</defs>
			<image
				width="100%"
				height="100%"
				preserveAspectRatio="xMidYMid slice"
				clip-path="url(#clipSquircle)"
				:xlink:href="imgUrl"
				@error="$event.target.href = 'https://ifh.cc/g/pLtMj2.png'"
			/>
		</svg>
	</div>
</template>

<script>
export default {
	name: "ProfileImg",
	props: {
		imgUrl: {
			type: String,
			default: "https://ifh.cc/g/pLtMj2.png",
		},
		width: {
			type: String,
			default: "50px",
		},
		height: String,
	},
};
</script>

<style scoped></style>
