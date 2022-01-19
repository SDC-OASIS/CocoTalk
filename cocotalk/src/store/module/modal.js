import createPersistedState from "vuex-persistedstate";

const modal = {
	plugins: [createPersistedState()],
	namespaced: true,
	state: {
		myProfileModal: "close",
		profileModal: {
			status: "close",
			data: Object,
		},
		addFriendModal: "close",
	},
	mutations: {
		GET_SCREEN(state, payload) {
			state.screenInfo.width = payload.width;
		},
		OPEN_PROFILE_MODAL(state, payload) {
			state.profileModal.status = payload.status;
			state.profileModal.userInfo = payload.userInfo;
		},
		CLOSE_PROFILE_MODAL(state) {
			state.profileModal.status = "close";
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
	},
	modules: {},
};

export default modal;
