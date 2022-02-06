import createPersistedState from "vuex-persistedstate";

const modal = {
	plugins: [createPersistedState()],
	namespaced: true,
	state: {
		alert: {
			status: "close",
			text: "",
		},
		addFriendModal: "close",
		makeChatModal: "close",
		roomNameModal: {
			status: "open",
			selectedFriends: Object,
		},
		profileModal: {
			status: "close",
			userProfileInfo: Object,
		},
	},
	mutations: {
		GET_SCREEN(state, payload) {
			state.screenInfo.width = payload.width;
		},
		OPEN_ALERT(state, payload) {
			state.alert.status = payload.status;
			state.alert.text = payload.text;
		},
		CLOSE_ALERT(state) {
			state.alert.status = "close";
		},
		OPEN_PROFILE_MODAL(state, payload) {
			state.profileModal.status = payload.status;
			state.profileModal.userProfileInfo = payload.userProfileInfo;
		},
		CLOSE_PROFILE_MODAL(state) {
			state.profileModal.status = "close";
		},
		OPEN_ADD_FRIEND_MODAL(state, payload) {
			state.addFriendModal = payload;
		},
		CLOSE_ADD_FRIEND_MODAL(state) {
			state.addFriendModal = "close";
		},
		OPEN_MAKE_CHAT_MODAL(state, payload) {
			state.makeChatModal = payload;
		},
		CLOSE_MAKE_CHAT_MODAL(state) {
			state.makeChatModal = "close";
		},
		OPEN_ROOM_NAME_MODAL(state, payload) {
			state.roomNameModal.status = "open";
			state.roomNameModal.selectedFriends = payload;
		},
		CLOSE_ROOM_NAME_MODAL(state) {
			state.roomNameModal.status = "close";
		},
	},
	actions: {
		getScreen: function (context, payload) {
			context.commit("GET_SCREEN", payload);
		},
		openAlert: function (context, payload) {
			context.commit("OPEN_ALERT", payload);
		},
		closeAlert: function (context) {
			context.commit("CLOSE_ALERT");
		},
		openProfileModal: function (context, payload) {
			context.commit("OPEN_PROFILE_MODAL", payload);
		},
		closeProfileModal: function (context) {
			context.commit("CLOSE_PROFILE_MODAL");
		},
		openAddFriendModal: function (context, payload) {
			context.commit("OPEN_ADD_FRIEND_MODAL", payload);
		},
		closeAddFriendModal: function (context) {
			context.commit("CLOSE_ADD_FRIEND_MODAL");
		},
		openMakeChatModal: function (context, payload) {
			context.commit("OPEN_MAKE_CHAT_MODAL", payload);
		},
		closeMakeChatModal: function (context) {
			context.commit("CLOSE_MAKE_CHAT_MODAL");
		},
		openroomNameModal: function (context, payload) {
			console.log("채팅방 멤버 옮기기");
			console.log(payload);
			context.commit("OPEN_ROOM_NAME_MODAL", payload);
		},
		closeroomNameModal: function (context) {
			context.commit("CLOSE_ROOM_NAME_MODAL");
		},
	},
	modules: {},
};

export default modal;
