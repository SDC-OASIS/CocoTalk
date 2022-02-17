<template>
  <div class="modal row" @click.self="closeProfileModal">
    <div class="modal-container" :style="{ backgroundImage: `url(${userProfileInfo.profile.background})` }" @click.self="openFullImg(userProfileInfo.profile.background)">
      <!--파일 업로드 테스트용 START -->
      <div style="color: white">
        <div style="color: yellow">프로필 사진 업로드</div>
        <input id="customFile" type="file" @change="handleProfileFileChange" />
        <label class="custom-file-label" for="customFile">{{ file1_name }}</label>
      </div>
      <div style="color: white">
        <div style="color: yellow">배경 사진 업로드</div>
        <input id="customFile" type="file" @change="handleBGFileChange" />
        <label class="custom-file-label" for="customFile">{{ file2_name }}</label>
      </div>
      <!--파일 업로드 테스트용 END -->
      <div @click="closeProfileModal">
        <span class="iconify exit" data-icon="bx:bx-x" style="color: white"></span>
      </div>
      <div class="modal-bottom-container">
        <div class="profile-modal-info" @click.self="openFullImg(userProfileInfo.profile.background)">
          <div style="cursor: pointer; display: inline-block" @click="openFullImg(userProfileInfo.profile.profile)">
            <profile-img :imgUrl="userProfileInfo.profile.profile" width="70px" />
          </div>
          <br />
          <span>{{ userProfileInfo.username }}</span>
          <br />
          <span v-if="userProfileInfo.profile.message">{{ userProfileInfo.profile.message }}</span>
          <div v-else style="height: 20px; width: 10px"></div>
        </div>
        <hr />
        <div class="modal-profile-chat" style="display: inline-block; margin: 0 20px">
          <div @click="startPrivateChat" style="display: inline-block; margin: 0 20px">
            <i class="chat fas fa-comment"></i>
            <div style="font-size: 13px">1:1 채팅</div>
          </div>
          <div v-if="userProfileInfo.id == userInfo.id" style="display: inline-block; font-size: 20px; margin: 0 20px">
            <i class="fas fa-pen" style="margin: 10px 0"></i>
            <div style="font-size: 13px">프로필 관리</div>
          </div>
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
      //파일 업로드 테스트용 변수
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
    /////////////// 파일 업로드 테스트용 함수
    handleProfileFileChange(e) {
      this.file1_name = e.target.files[0].name;
      this.$store.dispatch("userStore/updateProfile", e.target.files[0]);
    },
    handleBGFileChange(e) {
      this.file2_name = e.target.files[0].name;
      this.$store.dispatch("userStore/updateBG", e.target.files[0]);
    },
    /////////////////
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
      console.log("프로필에서 클릭해 1대1채팅열기");
      console.log(this.profileInfo);
      this.$store.dispatch("socket/startPrivateChat", this.profileInfo, { root: true });
      this.$store.dispatch("modal/setSidebar", false, { root: true });
    },
  },
  created() {
    console.log(this.profileInfo);
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
}
</style>
