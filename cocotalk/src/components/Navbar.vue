<template>
	<div class="nav-outer-container row">
		<div class="nav-logo">
			<img src="@/assets/logo.png" alt="logo" />
		</div>
		<div class="nav-inner-container">
			<ul class="nav row">
				<li>
					<router-link to="/friends">
						<span class="iconify" data-icon="fa-solid:user-friends"></span>
					</router-link>
				</li>
				<li>
					<router-link to="/chats">
						<span class="iconify" data-icon="ant-design:message-filled"></span>
					</router-link>
				</li>
				<li>
					<router-link to="/friends/setting">
						<span class="iconify" data-icon="bi:bell-fill"></span>
					</router-link>
				</li>
				<li>
					<router-link to="/chats/setting">
						<span class="iconify" data-icon="uil:setting"></span>
					</router-link>
				</li>
				<li>
					<a href="#">
						<span class="iconify" data-icon="fe:logout"></span>
					</a>
				</li>
			</ul>
		</div>
	</div>
</template>

<script>
export default {};
</script>

<style scoped>
.nav-outer-container {
	justify-content: center;
	background: #749f58;
}
.nav-inner-container {
	width: 650px;
}
.nav-logo {
	width: 400px;
}
.nav-logo img {
	width: 50px;
	float: left;
}
ul.nav {
	margin: 0;
	padding: 0;
	list-style: none;
	justify-content: space-between;
}
ul.nav li {
	float: left;
}
ul.nav a {
	display: block;
	padding: 14px 50px;
	color: #ffffff;
	font-size: 30px;
	line-height: 28px;
}
@media (max-width: 1000px) {
	ul.nav a {
		padding: 14px 40px;
	}
}
@media (max-width: 850px) {
	ul.nav a {
		padding: 14px 30px;
	}
}
@media (max-width: 700px) {
	ul.nav a {
		padding: 14px 20px;
	}
}
@media (max-width: 550px) {
	ul.nav a {
		padding: 14px 10px;
	}
}
ul.nav a:hover,
ul.nav li.current span {
	background: #42652b;
	height: 33px;
}
</style>
