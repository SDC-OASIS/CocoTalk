<template>
  <div class="modal row" @click.self="closeProfileModal">
    <div class="modal-container" :style="{ backgroundImage: `url(${userProfileInfo.profile.background})` }" @click.self="openFullImg(userProfileInfo.profile.background)">
      <!-- 프로필 편집 -->
      <div style="display: none">
        <input id="profileFile" type="file" @change="handleProfileFileChange" />
      </div>
      <div style="display: none">
        <input id="backgroundFile" type="file" @change="handleBGFileChange" />
      </div>
      <div @click="closeProfileModal">
        <span class="iconify exit" data-icon="bx:bx-x" style="color: white"></span>
      </div>
      <!-- 프로필모달 하단부 -->
      <div class="modal-bottom-container">
        <div class="profile-modal-info" @click.self="openFullImg(userProfileInfo.profile.background)">
          <!-- 프로필 이미지 -->
          <div style="cursor: pointer; display: inline-block; position: relative">
            <profile-img :imgUrl="userProfileInfo.profile.profile" width="70px" @click.native="openFullImg(userProfileInfo.profile.profile)" />
            <label class="custom-file-label" for="profileFile">
              <div class="profile-camera-container" v-if="profileEditStatus" for="profileFile">
                <i class="profile-camera fas fa-camera" style="margin: 10px 0"></i>
              </div>
            </label>
          </div>
          <br />
          <span>{{ userProfileInfo.username }}</span>
          <br />
          <span v-if="userProfileInfo.profile.message">{{ userProfileInfo.profile.message }}</span>
          <div v-else style="height: 20px; width: 10px"></div>
        </div>
        <hr />
        <!-- 프로필 모달 하단부 관리 -->
        <div class="modal-profile-chat" style="display: inline-block; margin: 0 20px">
          <div v-if="!profileEditStatus" @click="startPrivateChat" style="display: inline-block; margin: 0 20px">
            <i class="chat fas fa-comment"></i>
            <div style="font-size: 13px">1:1 채팅</div>
          </div>
          <div v-if="userProfileInfo.id == userInfo.id && !profileEditStatus" style="display: inline-block; font-size: 20px; margin: 0 20px" @click="selectImage">
            <i class="fas fa-pen" style="margin: 10px 0"></i>
            <div style="font-size: 13px">프로필 관리</div>
          </div>
          <label class="custom-file-label" for="backgroundFile">
            <div v-if="profileEditStatus">
              <i class="background-camera fas fa-camera" style="margin: 10px 0"></i>
            </div>
          </label>
        </div>
      </div>
    </div>
    <div v-if="fullImg.status" class="full-img" :style="{ backgroundImage: `url(${fullImg.img})` }">
      <div @click="closeFullImg">
        <span class="iconify exit" data-icon="bx:bx-x" style="color: white"></span>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../common/ProfileImg.vue";

export default {
  name: "ProfileModal",
  components: {
    ProfileImg,
  },
  data() {
    return {
      fullImg: {
        status: false,
        img: "",
      },
      profileInfo: this.userProfileInfo,
      profileEditStatus: false,
      file1_name: "파일을 선택하세요.",
      message1: "Hello, world",
      file2_name: "파일을 선택하세요.",
      message2: "Hello, world",
    };
  },
  props: {
    userProfileInfo: Object,
  },
  computed: {
    ...mapState("userStore", ["userInfo"]),
    ...mapState("modal", ["profileModal"]),
  },
  methods: {
    handleProfileFileChange(e) {
      this.file1_name = e.target.files[0].name;
      this.$store.dispatch("userStore/updateProfile", e.target.files[0]);
      this.$store.dispatch("socket/getChatList");
    },
    handleBGFileChange(e) {
      this.file2_name = e.target.files[0].name;
      this.$store.dispatch("userStore/updateBG", e.target.files[0]);
      this.$store.dispatch("socket/getChatList");
    },
    closeProfileModal() {
      this.$store.dispatch("modal/closeProfileModal");
    },
    openFullImg(data) {
      this.fullImg.status = true;
      this.fullImg.img = data;
    },
    closeFullImg() {
      this.fullImg.status = false;
    },
    startPrivateChat() {
      this.$store.dispatch("socket/startPrivateChat", this.profileInfo, { root: true });
      this.$store.dispatch("modal/setSidebar", false, { root: true });
    },
    selectImage() {
      this.profileEditStatus = true;
    },
    backgroundImg() {
      if (this.userProfileInfo.profile.background) {
        return this.userProfileInfo.profile.background;
      }
    },
    profileImg() {
      if (this.userProfileInfo.profile.profile) {
        return this.userProfileInfo.profile.profile;
      }
    },
  },
};
</script>

<style scoped>
.modal-container {
  width: 400px;
  height: 600px;
  background-color: #22291c;
  border-radius: 10px;
  position: relative;
  cursor: pointer;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}
.exit {
  position: absolute;
  top: 0;
  right: 0;
  margin: 13px;
  font-size: 30px;
  cursor: pointer;
}
.modal-bottom-container {
  width: 100%;
  position: absolute;
  bottom: 0;
  padding: 40px 0;
  color: #ffffff;
}
.modal-bottom-container > hr {
  border: 1px solid #ecebeb;
}
.profile-modal-info {
  padding-bottom: 5px;
}
.profile-modal-info > div {
  padding: 3px;
}
.modal-profile-chat:hover {
  cursor: auto;
}
.modal-profile-chat > div:hover {
  color: #ccc8c8;
  cursor: pointer;
}

.chat {
  font-size: 23px;
  margin: 10px;
}
.full-img {
  width: 400px;
  height: 600px;
  background-color: #22291c;
  border-radius: 10px;
  position: absolute;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}

.profile-camera-container {
  position: absolute;
  top: 25px;
  right: -20px;
  padding: 8px;
  border-radius: 20px;
  font-size: 25px;
}
.profile-camera {
  padding: 8px;
  border-radius: 20px;
  font-size: 15px;
  background-color: #254e0a;
}
.profile-camera:hover {
  background-color: #a7b69e;
  cursor: pointer;
}

.background-camera {
  padding: 8px;
  border-radius: 20px;
  font-size: 20px;
  background-color: #254e0a;
}
.background-camera:hover {
  background-color: #a7b69e;
  cursor: pointer;
}
</style>
