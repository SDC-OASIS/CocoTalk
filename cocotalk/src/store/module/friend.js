import createPersistedState from "vuex-persistedstate";
import store from "../../store";
import router from "../../router";
import axios from "@/utils/axios";

const friend = {
  namespaced: true,
  plugins: [createPersistedState()],
  state: {
    friends: [],
  },
  getters: {
    friends: (state) => {
      state.friends;
    },
  },
  mutations: {
    GET_FRIENDS(state, payload) {
      state.friends = payload;
    },
    ADD_FRIEND(state, payload) {
      state.friends.push(payload);
    },
  },
  actions: {
    getFriends: function (context) {
      axios.get("user/friends").then((res) => {
        console.log("친구목록 가져오기");
        let friends = res.data.data;
        // 친구가 1명이라도 존재하는 경우 STRING jSON 파싱
        if (friends.length) {
          friends.forEach((e) => {
            e.friend.profile = JSON.parse(e.friend.profile);
          });
        }
        context.commit("GET_FRIENDS", friends);
        console.log(friends);
      });
    },
    addFriend: function (context, friendId) {
      axios.post("user/friends", friendId).then((res) => {
        console.log("친구추가성공");
        let friendInfo = res.data.data.toUser;
        friendInfo.profile = JSON.parse(friendInfo.profile);
        console.log(friendInfo);
        store.dispatch("modal/closeAddFriendModal");
        router.go();
      });
      // context.commit("ADD_FRIEND", friendInfo);
    },
  },
  modules: {},
};

export default friend;
