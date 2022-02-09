import Vue from "vue";
import VueRouter from "vue-router";
import Login from "../views/Login.vue";
import MainPage from "../views/MainPage.vue";
import FriendList from "../views/FriendList.vue";
import ChatRoomList from "../views/ChatRoomList.vue";
import ChatRoom from "../views/ChatRoom.vue";
import BeforeEnterChat from "../views/BeforeEnterChat.vue";
import Setting from "../views/Setting.vue";
import PageNotFound from "../views/PageNotFound.vue";
import store from "../store";

Vue.use(VueRouter);

const routes = [
	{
		path: "/login",
		name: "login",
		components: {
			login: Login,
		},
	},
	{
		path: "/",
		name: "MainPage",
		component: MainPage,
		meta: { auth: true },
		children: [
			{
				path: "/friends",
				name: "friends",
				components: {
					left: FriendList,
					right: BeforeEnterChat,
				},
				beforeEnter: (to, from, next) => {
					store.dispatch("chat/changeMainPage", "friends", { root: true });
					store.dispatch("friend/getFriends", "friends", { root: true });
					return next();
				},
			},
			{
				path: "/chats",
				name: "chats",
				components: {
					left: ChatRoomList,
					right: BeforeEnterChat,
				},
			},
			{
				path: "/friends/setting",
				name: "friendsSetting",
				components: {
					left: FriendList,
					right: Setting,
				},
			},
			{
				path: "/chats/setting",
				name: "chatsSetting",
				components: {
					left: ChatRoomList,
					right: Setting,
				},
			},
			{
				path: "/friends/:chat/:roomId?",
				name: "friendsChat",
				components: {
					left: FriendList,
					right: ChatRoom,
				},
			},
			{
				path: "/chats/:chat/:roomId?",
				name: "chatsChat",
				components: {
					left: ChatRoomList,
					right: ChatRoom,
				},
			},
			{
				path: "/error",
				name: "Error",
				components: {
					error: PageNotFound,
				},
				beforeEnter: (to, from, next) => {
					store.dispatch("chat/changeMainPage", "error", { root: true });
					return next();
				},
			},
		],
	},
	{
		path: "*",
		redirect: "/error",
	},
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
