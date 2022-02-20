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
      <add-friend-modal v-if="addFriendModal == 'open'" />
      <alert v-if="alert.status == 'open'" :text="alert.text" />
      <chat-creation-modal v-if="ChatCreationModal == 'open'" />
      <invite-friend-modal v-if="inviteFriendModal == 'open'" />
      <room-name-edit-modal v-if="roomNameEditModal.status == 'open'" />
      <private-to-team-modal v-if="privateToTeamModal.status == 'open'" />
      <profile-modal v-if="profileModal.status == 'open'" :userProfileInfo="profileModal.userProfileInfo" />
      <sidebar-files-modal v-if="sidebarFilesModal.status == 'open'" :files="sidebarFilesModal.files" />
    </div>
  </div>
</template>

<script>
import { mapMutations, mapState } from "vuex";
import Navbar from "@/components/Navbar.vue";
import Alert from "@/components/modals/Alert.vue";
import ProfileModal from "@/components/modals/ProfileModal.vue";
import AddFriendModal from "@/components/modals/AddFriendModal.vue";
import InviteFriendModal from "@/components/modals/InviteFriendModal.vue";
import ChatCreationModal from "@/components/modals/ChatCreationModal.vue";
import RoomNameEditModal from "@/components/modals/RoomNameEditModal.vue";
import SidebarFilesModal from "@/components/modals/SidebarFilesModal.vue";
import PrivateToTeamModal from "@/components/modals/PrivateToTeamModal.vue";

export default {
  name: "MainPage",
  data() {
    return {
      nav: true,
    };
  },
  components: {
    Alert,
    Navbar,
    ProfileModal,
    AddFriendModal,
    ChatCreationModal,
    RoomNameEditModal,
    InviteFriendModal,
    PrivateToTeamModal,
    SidebarFilesModal,
  },
  created() {
    this.$store.dispatch("socket/getChatList");
    this.$store.dispatch("socket/checkConnect");
    this.$store.dispatch("socket/chatListConnect");
    // 에러페이지에서는 navbar 안보이게 만들기
    if (window.location.pathname == "/error") {
      this.nav = false;
    }
  },
  destroyed() {
    this.CLOSE_ALERT,
      this.CLOSE_PROFILE_MODAL,
      this.CLOSE_ADD_FRIEND_MODAL,
      this.CLOSE_CHAT_CREATION_MODAL,
      this.CLOSE_ROOM_NAME_EDIT_MODAL,
      this.CLOSE_INVITE_FRIEND_MODAL,
      this.CLOSE_PRIVATE_TO_TEAM_MODAL;
    this.CLOSE_SIDEBAR_FILES_MODAL;
  },
  computed: {
    ...mapState("socket", ["stompChatListConnected", "stompChat"]),
    ...mapState("modal", ["alert", "addFriendModal", "profileModal", "ChatCreationModal", "roomNameEditModal", "inviteFriendModal", "privateToTeamModal", "sidebarFilesModal"]),
  },
  methods: {
    ...mapMutations("socket", ["SET_STOMP_CHAT_LIST_CONNECTED", "stompChatListClient"]),
    ...mapMutations("modal", [
      "CLOSE_ALERT",
      "CLOSE_PROFILE_MODAL",
      "CLOSE_ADD_FRIEND_MODAL",
      "CLOSE_CHAT_CREATION_MODAL",
      "CLOSE_ROOM_NAME_EDIT_MODAL",
      "CLOSE_INVITE_FRIEND_MODAL",
      "CLOSE_PRIVATE_TO_TEAM_MODAL",
      "OPEN_SIDEBAR_FILES_MODAL",
      "CLOSE_SIDEBAR_FILES_MODAL",
    ]),
  },
};
</script>

<style></style>
