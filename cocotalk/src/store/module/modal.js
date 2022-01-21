import createPersistedState from "vuex-persistedstate";

const modal = {
	plugins: [createPersistedState()],
	namespaced: true,
	state: {
		addFriendModal: "close",
		makeChatModal: "close",
		profileModal: {
			status: "close",
			userProfileInfo: Object,
		},
	},
	mutations: {
		GET_SCREEN(state, payload) {
			state.screenInfo.width = payload.width;
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
	},
	actions: {
		getScreen: function (context, payload) {
			context.commit("GET_SCREEN", payload);
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
	},
	modules: {},
};

export default modal;
