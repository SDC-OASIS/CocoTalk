<template>
  <div>
    <div id="sidebar" v-if="sidebar">
      <div class="sidebar-container">
        <div class="sidebar-content">
          <div class="sidebar-title">채팅방 서랍</div>
          <!-- 사진, 동영상 -->
          <div class="sidebar-item" @click="openSidebarFilesModal">
            <div>
              <i class="far fa-image"></i>
              <span> 사진, 동영상 </span>
            </div>
            <div class="row">
              <div class="media-img-container row" v-for="(message, idx) in imgMessages" :key="idx">
                <img :src="message.content" alt="" class="box" />
              </div>
            </div>
          </div>
          <!-- 파일 -->
          <div class="sidebar-item">
            <i class="fas fa-folder"></i>
            <span> 파일 </span>
          </div>
          <hr />
          <!-- 대화상대 -->
          <div>
            <div class="sidebar-title">대화상대</div>
            <div class="add-member row" @click="inviteFriend">
              <i class="far fa-plus-square" style="font-size: 50px"></i>
              <span style="padding: 0 10px"> 대화상대 초대 </span>
            </div>
            <div class="friend-container row" v-for="(member, idx) in roomInfo.members" :key="idx">
              <div @click="openProfileModal(member)">
                <profile-img :imgUrl="member.profile.profile" width="50px" @status="checkStatus" />
              </div>
              <div class="friend-name" :id="'check' + idx">{{ member.username }}</div>
            </div>
          </div>
        </div>
        <!-- footer -->
        <div class="sidebar-footer row">
          <div @click="exitChat">
            <span class="iconify exit-chat" data-icon="fe:logout"></span>
          </div>
          <div class="row">
            <i class="far fa-star"></i>
            <span class="iconify" data-icon="bi:bell-fill"></span>
          </div>
        </div>
      </div>
      <div class="sidebar-background" @click="closeSidebar"></div>
    </div>
    <div class="outer-background-container"></div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../common/ProfileImg.vue";

export default {
  name: "Sidebar",
  components: {
    ProfileImg,
  },
  data() {
    return {
      totalImgMessages: [],
    };
  },
  created() {},
  props: {
    roomInfo: [],
    chatMessages: [],
  },
  computed: {
    ...mapState("friend", ["friends"]),
    ...mapState("modal", ["sidebar"]),
    ...mapState("socket", ["inviteRoomInfo"]),
    imgMessages() {
      let imgs = [];
      for (let i = 0; i < this.chatMessages.length; i++) {
        if (this.chatMessages[i].type == 4) {
          imgs.push(this.chatMessages[i]);
        }
        if (imgs.length == 4) {
          break;
        }
      }
      return imgs;
    },
  },
  methods: {
    closeSidebar() {
      const sidebar = document.querySelector(".sidebar-container");
      const sidebarBack = document.querySelector(".sidebar-background");
      sidebarBack.style.display = "none";
      sidebar.style.right = "-402px";
    },
    openProfileModal(userProfileInfo) {
      this.$store.dispatch("modal/openProfileModal", { status: "open", userProfileInfo: userProfileInfo }, { root: true });
    },
    checkStatus(status) {
      if (!status) {
        this.closeSidebar();
      }
    },
    inviteFriend() {
      this.$store.dispatch("modal/openInviteFriendModal");
    },
    exitChat() {
      this.$store.dispatch("socket/exitChat", this.roomInfo, { root: true });
    },
    openSidebarFilesModal() {
      let totalImgs = [];
      for (let i = 0; i < this.chatMessages.length; i++) {
        if (this.chatMessages[i].type == 4) {
          totalImgs.push(this.chatMessages[i]);
        }
      }
      this.totalImgMessages = totalImgs;
      this.$store.dispatch("modal/openSidebarFilesModal", this.totalImgMessages, { root: true });
    },
  },
};
</script>

<style scoped>
.outer-background-container {
  position: absolute;
  top: 0;
  right: -400px;
  height: 100%;
  width: 400px;
  background-color: #fffacd;
  border-left: 2px solid #9eac95;
  z-index: 3;
}
.sidebar-container {
  position: absolute;
  top: 0;
  right: -400px;
  height: 100%;
  padding-bottom: 50px;
  width: 400px;
  border-left: 2px solid #9eac95;
  border-right: 2px solid #9eac95;
  background-color: #ffffff;
  z-index: 2;
  transition: 1s ease;
  text-align: left;
}
.sidebar-content {
  overflow: auto;
  height: 87%;
  padding-right: 5px;
  padding: 20px 5px 0 0;
}
.sidebar-content::-webkit-scrollbar {
  background-color: #ffffff;
  width: 18px;
}
.sidebar-content::-webkit-scrollbar-track {
  background-color: #ffffff;
  width: 10px;
}
.sidebar-content::-webkit-scrollbar-thumb {
  background-color: #b8c8ae;
  border-radius: 10px;
  width: 10px;
  background-clip: padding-box;
  border: 5px solid transparent;
}
.sidebar-background {
  position: absolute;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  background-color: rgb(0, 0, 0, 0.5);
  display: none;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  background-color: rgb(0, 0, 0, 0.5);
  display: none;
}

.sidebar-title {
  padding: 10px 20px 20px 20px;
  color: #42652b;
  font-size: 20px;
  font-weight: bold;
}
.sidebar-item {
  color: #90949b;
  padding: 10px 20px 10px 20px;
}
.sidebar-item:hover {
  background-color: #e7f7dd;
  cursor: pointer;
}
hr {
  border-color: #b8c8ae;
  margin: 10px;
}
.media-img-container {
  width: 85px;
  margin: 0;
}
.media-img-container > img {
  width: 65px;
  height: 65px;
  padding: 10px 10px 5px 0;
  margin: 0;
}
.add-member {
  padding: 10px 20px;
  font-size: 20px;
  color: #88a873;
  align-items: center;
  cursor: pointer;
  font-weight: bold;
}
.add-member:hover {
  background-color: #e7f7dd;
}
.friend-container {
  padding: 7px 20px;
  align-items: center;
  cursor: pointer;
}
.friend-container img {
  width: 50px;
  height: 50px;
}
.friend-name {
  font-weight: bold;
  padding-left: 15px;
}
.sidebar-footer {
  height: 10px;
  width: 360px;
  background-color: #b4d89d;
  position: fixed;
  bottom: 0;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  font-size: 24px;
  color: #42652b;
}
.sidebar-footer > div {
  width: 70px;
  justify-content: space-between;
  align-items: center;
  font-size: 22px;
}
@media (max-width: 1600px) {
  .sidebar-container {
    width: 350px;
  }
  .sidebar-footer {
    width: 310px;
  }
}
.exit-chat {
  cursor: pointer;
}
</style>
