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
		ChatCreationModal: "close",
		roomNameEditModal: {
			status: "close",
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
		OPEN_CHAT_CREATION_MODAL(state, payload) {
			state.ChatCreationModal = payload;
		},
		CLOSE_CHAT_CREATION_MODAL(state) {
			state.ChatCreationModal = "close";
		},
		OPEN_ROOM_NAME_EDIT_MODAL(state, payload) {
			state.roomNameEditModal.status = "open";
			state.roomNameEditModal.selectedFriends = payload;
		},
		CLOSE_ROOM_NAME_EDIT_MODAL(state) {
			state.roomNameEditModal.status = "close";
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
		openChatCreationModal: function (context, payload) {
			context.commit("OPEN_CHAT_CREATION_MODAL", payload);
		},
		closeChatCreationModal: function (context) {
			context.commit("CLOSE_CHAT_CREATION_MODAL");
		},
		openRoomNameEditModal: function (context, payload) {
			console.log("채팅방 멤버 옮기기");
			console.log(payload);
			context.commit("OPEN_ROOM_NAME_EDIT_MODAL", payload);
		},
		closeRoomNameEditModal: function (context) {
			context.commit("CLOSE_ROOM_NAME_EDIT_MODAL");
		},
	},
	modules: {},
};

export default modal;
