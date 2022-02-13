<template>
  <div id="mainPage">
    <div v-if="this.stompChatListConnected">
      <router-view name="error" />
      <div>
        <navbar v-if="nav" />
        <div class="content-container">
          <router-view name="left" class="left-container" />
          <router-view name="right" class="right-container" />
        </div>
      </div>
      <alert v-if="alert.status == 'open'" :text="alert.text" />
      <add-friend-modal v-if="addFriendModal == 'open'" />
      <chat-creation-modal v-if="ChatCreationModal == 'open'" />
      <room-name-edit-modal v-if="roomNameEditModal.status == 'open'" />
      <profile-modal v-if="profileModal.status == 'open'" :userProfileInfo="profileModal.userProfileInfo" />
    </div>
  </div>
</template>

<script>
import { mapMutations, mapState } from "vuex";
import Navbar from "@/components/Navbar.vue";
import Alert from "@/components/modals/Alert.vue";
import ProfileModal from "@/components/modals/ProfileModal.vue";
import AddFriendModal from "@/components/modals/AddFriendModal.vue";
import ChatCreationModal from "@/components/modals/ChatCreationModal.vue";
import RoomNameEditModal from "@/components/modals/RoomNameEditModal.vue";

export default {
  name: "MainPage",
  data() {
    return {
      nav: true,
    };
  },
  components: {
    Navbar,
    AddFriendModal,
    ChatCreationModal,
    RoomNameEditModal,
    ProfileModal,
    Alert,
  },
  created() {
    this.setStompChatListDisconnect();
    this.$store.dispatch("socket/chatListConnect");
    // 에러페이지에서는 navbar 안보이게 만들기
    if (window.location.pathname == "/error") {
      this.nav = false;
    }
  },
  computed: {
    ...mapState("chat", ["friends", "roomStatus"]),
    ...mapState("userStore", ["screenInfo", "userInfo"]),
    ...mapState("socket", ["stompChatListConnected"]),
    ...mapState("modal", ["alert", "addFriendModal", "profileModal", "ChatCreationModal", "roomNameEditModal"]),
  },
  methods: {
    ...mapMutations("socket", ["setStompChatListClient", "setStompChatListConnected", "setStompChatListDisconnect"]),
  },
};
</script>

<style></style>
