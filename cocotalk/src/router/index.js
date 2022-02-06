import Vue from "vue";
import VueRouter from "vue-router";
import Login from "../views/Login.vue";
import Friends from "../views/Friends.vue";
import Chats from "../views/Chats.vue";
import Chat from "../views/Chat.vue";
import ChatDefault from "../views/ChatDefault.vue";
import Setting from "../views/Setting.vue";
import NoPage from "../views/NoPage.vue";
import store from "../store";

Vue.use(VueRouter);

const routes = [
	{
		path: "/",
		name: "login",
		components: {
			login: Login,
		},
	},
	{
		path: "/friends",
		name: "friends",
		components: {
			left: Friends,
			right: ChatDefault,
		},
	},
	{
		path: "/chats",
		name: "chats",
		components: {
			left: Chats,
			right: ChatDefault,
		},
	},
	{
		path: "/friends/setting",
		name: "friendsSetting",
		components: {
			left: Friends,
			right: Setting,
		},
	},
	{
		path: "/chats/setting",
		name: "chatsSetting",
		components: {
			left: Chats,
			right: Setting,
		},
	},
	{
		path: "/friends/:chat/:roomId?",
		name: "friendsChat",
		components: {
			left: Friends,
			right: Chat,
		},
	},
	{
		path: "/chats/:chat/:roomId?",
		name: "chatsChat",
		components: {
			left: Chats,
			right: Chat,
		},
	},
	{
		path: "/error",
		name: "Error",
		components: {
			error: NoPage,
		},
		beforeEnter: () => {
			store.dispatch("chat/changeMainPage", "error", { root: true });
		},
	},
	{
		path: "*",
		redirect: "/error",

		// 아래처럼 바로 NotFound 경로를 매칭해도 됨
		// component: NotFound
	},

	// },
	// {
	// 	path: "/friends/chat/:roomId?",
	// 	name: "friendList",
	// 	component: FriendList,
	// },
	// {
	// 	path: "/chatlist/chat/:roomId?",
	// 	name: "chatList",
	// 	component: ChatList,
	// },
];

const router = new VueRouter({
	mode: "history",
	base: process.env.BASE_URL,
	routes,
});

export default router;
