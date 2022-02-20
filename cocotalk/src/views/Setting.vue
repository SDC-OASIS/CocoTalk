<template>
  <div class="chat-default-container row">
    <h1 class="setting-title">CocoTalk Developers</h1>
    <img class="polaroid" src="https://d1fwng7137yw58.cloudfront.net/common/team_oasis.jpg" alt="" />
    <div style="width: 100%">
      <h3 class="setting-title" style="margin-bottom: 0px">고병학</h3>
      <span class="setting-title">iOS 개발자</span>
      <h3 class="setting-title">권희은</h3>
      <span class="setting-title">프론트엔드 개발자</span>
      <h3 class="setting-title">김민정</h3>
      <span class="setting-title">백엔드 개발자</span>
      <h3 class="setting-title">황종훈</h3>
      <span class="setting-title">백엔드 개발자</span>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "Setting",
  data() {
    return {
      now: this.$route.params.roomId,
    };
  },
  computed: {
    ...mapState("chat", ["roomStatus"]),
    ...mapState("friends", ["friends"]),
  },
  methods: {},
};
</script>

<style scoped>
.chat-default-container {
  background-color: #ffffff;
  height: 100vh;
  flex-wrap: wrap;
  justify-content: center;
  align-content: center;
}
.chat-default-container > img {
  width: 320px;
  height: 220px;
}
.chat-default-container > span {
  padding-bottom: 150px;
  font-size: 25px;
  font-weight: bold;
  color: #90949b;
}
.polaroid {
  background: #000; /*Change this to a background image or remove*/
  border: solid #fff;
  border-width: 6px 6px 20px 6px;
  box-shadow: 1px 1px 5px #333; /* Standard blur at 5px. Increase for more depth */
  -webkit-box-shadow: 1px 1px 5px #333;
  -moz-box-shadow: 1px 1px 5px #333;
  height: 200px; /*Set to height of your image or desired div*/
  width: 200px; /*Set to width of your image or desired div*/
}
.setting-title {
  color: #728566;
}
</style>
