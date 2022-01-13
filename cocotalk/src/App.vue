<template>
	<div id="app">
		<div id="nav">
<<<<<<< HEAD
			<router-link to="/">Home</router-link> |
			<router-link to="/about">About</router-link>
		</div>
		<router-view />
=======
			<router-link to="/friends">친구목록</router-link> | <router-link to="/chats">메세지목록</router-link>|
			<router-link to="/friends/setting">설정</router-link>|
			<router-link to="/chats/setting">설정</router-link>
		</div>
		<router-view name="left" />
		<router-view name="right" />
>>>>>>> fdd4c1e175380169df75ca4d56312d900f0e5506
	</div>
</template>

<script>
export default {};
</script>

<style>
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #2c3e50;
}

#nav {
	padding: 30px;
}

#nav a {
	font-weight: bold;
	color: #2c3e50;
}

#nav a.router-link-exact-active {
	color: #42b983;
}
</style>
