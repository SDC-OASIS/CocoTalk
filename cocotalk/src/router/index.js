import Vue from "vue";
import VueRouter from "vue-router";
import Friends from "../views/Friends.vue";
import Chats from "../views/Chats.vue";
import Chat from "@/views/Chat.vue";
import ChatDefault from "@/views/ChatDefault.vue";
import Setting from "@/views/Setting.vue";

Vue.use(VueRouter);

const routes = [
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
		path: "/friends/chat/:roomId?",
		name: "friendsChat",
		components: {
			left: Friends,
			right: Chat,
		},
	},
	{
		path: "/chatlist/chat/:roomId?",
		name: "chatsChat",
		components: {
			left: Chats,
			right: Chat,
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
