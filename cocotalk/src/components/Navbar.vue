<template>
  <div class="nav-outer-container row">
    <div class="nav-logo">
      <img src="@/assets/logo.png" alt="logo" />
    </div>
    <div class="nav-inner-container">
      <ul class="nav row">
        <li>
          <router-link :to="{ path: '/friends' + changePath }">
            <span class="iconify" data-icon="fa-solid:user-friends"></span>
          </router-link>
        </li>
        <li>
          <router-link :to="{ path: '/chats' + changePath }">
            <span class="iconify" data-icon="ant-design:message-filled"></span>
          </router-link>
        </li>
        <li v-show="!isMute">
          <router-link @click.native="setMute(true)" to="#">
            <span class="iconify" data-icon="bi:bell-fill"></span>
          </router-link>
        </li>
        <li v-show="isMute">
          <router-link @click.native="setMute(false)" to="#">
            <span class="iconify" data-icon="bi:bell-slash-fill" style="color: #ffc978"></span>
            <!-- <span class="iconify" data-icon="bi:bell-slash"></span> -->
          </router-link>
        </li>
        <li>
          <router-link :to="{ path: getMainPath + '/developers' }">
            <i class="iconify fa fa-desktop"></i>
            <!-- <span class="iconify" data-icon="uil:setting"></span> -->
          </router-link>
        </li>
        <li @click="logout">
          <router-link to="/login">
            <span class="iconify" data-icon="fe:logout"></span>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters } from "vuex";

export default {
  created() {
    //서비스 워커의 mute 상태 vuex 값으로 초기화
    this.setMute(this.isMute);
  },
  computed: {
    ...mapState("chat", ["roomStatus"]),
    ...mapGetters("workerStore", ["isMute"]),
    changePath() {
      // roomId가 있는 경우 == 채팅창이 열려있는 경우
      if (this.roomStatus.roomId) {
        return `/${this.roomStatus.chatPage}/${this.roomStatus.roomId}`;
      }
      // 채팅창이 닫혀있는 경우
      else {
        return "";
      }
    },
    getMainPath() {
      return `/${this.roomStatus.mainPage}`;
    },
  },
  methods: {
    logout() {
      this.$store.dispatch("userStore/logout", { root: true });
    },
    setMute(flag) {
      this.$store.dispatch("workerStore/setMute", flag);
      console.log("[VUEX GETTER] isMute", this.isMute);
    },
  },
};
</script>

<style scoped>
.nav-outer-container {
  justify-content: center;
  background: #749f58;
  display: float;
  z-index: 4;
}
.nav-inner-container {
  width: 550px;
}
.nav-logo {
  width: 380px;
}
.nav-logo img {
  width: 50px;
  float: left;
}
ul.nav {
  margin: 0;
  padding: 0;
  list-style: none;
  justify-content: space-between;
}
ul.nav li {
  float: left;
}
ul.nav a {
  display: block;
  padding: 12px 39px;
  color: #ffffff;
  font-size: 30px;
  line-height: 23px;
}
@media (max-width: 1600px) {
  .nav-inner-container {
    width: 500px;
  }
  .nav-logo {
    width: 330px;
  }
  ul.nav a {
    padding: 10px 30px;
  }
  .iconify {
    font-size: 25px;
  }
}
@media (max-width: 850px) {
  ul.nav a {
    padding: 14px 30px;
  }
}
@media (max-width: 700px) {
  ul.nav a {
    padding: 14px 20px;
  }
}
@media (max-width: 550px) {
  ul.nav a {
    padding: 14px 10px;
  }
}
ul.nav a:hover {
  background: #42652b;
  height: 30px;
}
</style>
