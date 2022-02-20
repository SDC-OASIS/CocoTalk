<template>
  <div class="friend-list-outer-container">
    <!-- 상단바 -->
    <div class="header row">
      <span>친구</span>
      <div class="header-icon-container row">
        <div style="dispaly: inline-block" @click="filterOn">
          <span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa" @click="filterOn"></span>
        </div>
        <div style="dispaly: inline-block" @click="openAddFriendModal">
          <span class="iconify" data-icon="heroicons-outline:user-add" style="color: #aaaaaa"></span>
        </div>
      </div>
    </div>
    <div v-if="filterStatus" class="add-friend-modal-input row">
      <input placeholder="이름을 입력하세요." maxlength="20" @input="filter = $event.target.value" @keyup="setFilter" />
    </div>
    <!-- 목록 -->
    <div class="friend-list-inner-container">
      <!-- 내 프로필 -->
      <div class="myprofile row">
        <div @click="openProfileModal(userInfo)">
          <profile-img :imgUrl="userInfo.profile.profile" width="50px" />
        </div>
        <friend-list-user-info :userInfo="userInfo" />
      </div>
      <hr />
      <!-- 친구목록 -->
      <div class="friend-list-container">
        <div class="friend-cnt">친구 - {{ friendsCnt }}</div>
        <div class="friend-list-item-container row" v-for="(friend, idx) in friendsFiltered" :key="idx" @dblclick="startPrivateChat(friend)">
          <div @click="openProfileModal(friend.friend)">
            <profile-img :imgUrl="friend.friend.profile.profile" width="40px" />
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
import Hangul from "hangul-js";

export default {
  name: "FriendList",
  components: {
    ProfileImg,
    FriendListUserInfo,
  },
  data() {
    return {
      filter: "",
      friendsFiltered: [],
      filterStatus: false,
    };
  },
  created() {
    this.$store.dispatch("chat/changeMainPage", "friends", { root: true });
    this.getUser();
    this.getFriends();
    this.friendsFiltered = this.friends; //이름으로 검색시 친구목록 필터
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
    filterOn() {
      this.filterStatus = !this.filterStatus;
    },
    setFilter() {
      let filteredFriends = [];
      if (this.filter != "") {
        this.friends.forEach((e) => {
          if (Hangul.search(e.friend.username, this.filter) >= 0) {
            filteredFriends.push(e);
          }
        });
        return (this.friendsFiltered = filteredFriends);
      }
      return (this.friendsFiltered = this.friends);
    },
    startPrivateChat(friend) {
      this.$store.dispatch("socket/startPrivateChat", friend.friend, { root: true });
    },
    openAddFriendModal() {
      this.$store.dispatch("modal/openAddFriendModal", "open", { root: true });
    },
    openProfileModal(userProfileInfo) {
      this.$store.dispatch("modal/openProfileModal", { status: "open", userProfileInfo: userProfileInfo }, { root: true });
    },
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

.add-friend-modal-input {
  text-align: center;
  border-radius: 20px;
  height: 35px;
  background: #d8eec0;
  align-items: center;
  justify-content: center;
  margin: 0 10px;
  margin-bottom: 10px;
}
.add-friend-modal-input > input {
  /* display: block; */
  border: none;
  border-radius: 20px;
  /* margin: 0 0 20px 0; */
  padding: 0 8%;
  width: 90%;
  /* height: 35px; */
  background: #d8eec0;
  font-size: 20px;
}
.add-friend-modal-input > input::placeholder {
  font-size: 17px;
  color: #749f58;
}
.add-friend-modal-input > input:focus {
  outline: none;
  /* outline: 2px solid #fce41e; */
}
</style>
