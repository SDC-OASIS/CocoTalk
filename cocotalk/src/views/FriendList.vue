<template>
  <div class="friend-list-outer-container">
    <div class="header row">
      <span>친구</span>
      <div class="header-icon-container row">
        <div style="dispaly: inline-block">
          <span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
        </div>
        <div style="dispaly: inline-block" @click="openAddFriendModal">
          <span class="iconify" data-icon="heroicons-outline:user-add" style="color: #aaaaaa"></span>
        </div>
      </div>
    </div>
    <div class="friend-list-inner-container">
      <div class="myprofile row">
        <div @click="openProfileModal(userInfo)">
          <profile-img :imgUrl="userInfo.profile.profile" width="60px" />
        </div>
        <friend-list-user-info :userInfo="userInfo" />
      </div>
      <hr />
      <div class="friend-list-container">
        <div class="friend-cnt">친구 - {{ friendsCnt }}</div>
        <div class="friend-list-item-container row" v-for="(friend, idx) in friends" :key="idx">
          <div @click="openProfileModal(friend.friend)">
            <profile-img :imgUrl="friend.friend.profile.profile" width="50px" />
          </div>
          <friend-list-user-info :userInfo="friend.friend" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ProfileImg from "@/components/common/ProfileImg.vue";
import FriendListUserInfo from "@/components/friends/FriendListUserInfo.vue";

export default {
  name: "FriendList",
  components: {
    ProfileImg,
    FriendListUserInfo,
  },
  mounted() {
    this.$store.dispatch("chat/changeMainPage", "friends", { root: true });
  },
  computed: {
    ...mapState("chat", ["roomStatus"]),
    ...mapState("friend", ["friends"]),
    ...mapState("userStore", ["userInfo", "screenInfo"]),
    friendsCnt() {
      return this.friends.length;
    },
  },
  methods: {
    ...mapActions("userStore", ["getUser"]),
    ...mapActions("friend", ["getFriends"]),
    openProfileModal(userProfileInfo) {
      this.$store.dispatch("modal/openProfileModal", { status: "open", userProfileInfo: userProfileInfo }, { root: true });
    },
    openAddFriendModal() {
      this.$store.dispatch("modal/openAddFriendModal", "open", { root: true });
    },
  },
  created() {
    console.log("=========[친구목록페이지]==========");
    if (screen.width <= 1600) {
      this.width = "60px";
    }
    this.$store.dispatch("chat/changeMainPage", "friends", { root: true });
    this.getUser();
    this.getFriends();
  },
};
</script>

<style scoped>
.friend-list-outer-container {
  display: block;
  padding-top: 20px;
  background-color: #ffffff;
  border-left: 2px solid #9eac95;
  border-right: 2px solid #9eac95;
  font-size: 15px;
}
.friend-list-outer-container > hr {
  border-bottom: 1px solid #9eac95;
  width: 90%;
}
.header {
  justify-content: space-between;
  padding-bottom: 10px;
}
.header-icon-container {
  width: 75px;
  justify-content: space-between;
  align-items: center;
}
.header > span {
  color: #749f58;
  font-size: 25px;
  font-weight: bold;
  padding-left: 20px;
}
.header > div {
  font-size: 23px;
  font-weight: bold;
  margin-right: 20px;
}
.iconify {
  padding: 7px;
  border-radius: 15px;
}
.iconify:hover {
  background-color: #e7f7dd;
  cursor: pointer;
}

.myprofile {
  padding: 10px 20px 10px 20px;
  align-items: center;
  cursor: pointer;
}

.myprofile:hover {
  background-color: #e7f7dd;
}
.myprofile > div {
  display: inline-block;
}

.friend-list-container {
  text-align: left;
}
.friend-list-inner-container {
  overflow: auto;
  display: block;
  height: 85vh;
}
.friend-list-inner-container::-webkit-scrollbar {
  background-color: #ffffff;
  width: 18px;
}
.friend-list-inner-container::-webkit-scrollbar-track {
  background-color: #ffffff;
  width: 10px;
}
.friend-list-inner-container::-webkit-scrollbar-thumb {
  background-color: #b8c8ae;
  border-radius: 10px;
  width: 10px;
  background-clip: padding-box;
  border: 5px solid transparent;
}
.friend-cnt {
  /* margin-bottom: 100px; */
  /* 왜 안되지! */
  font-size: 13px;
  margin: 15px 0px 5px 20px;
}
.friend-list-item-container {
  padding: 7px 0;
  align-items: center;
  cursor: pointer;
  padding-left: 20px;
}
.friend-list-item-container:hover {
  background-color: #e7f7dd;
}
.friend-list-item-container img {
  width: 50px;
  height: 50px;
}
</style>
