<template>
  <div class="chat-default-container row">
    <h1>설정페이지입니다.</h1>
    <button @click="check">ss</button>
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
  created() {
    console.log("설정 페이지");
  },

  computed: {
    ...mapState("chat", ["roomStatus"]),
    ...mapState("friends", ["friends"]),
  },
  watch: {
    "$route.params.roomId": function () {
      console.log("채팅을 시작해주세요");
    },
  },
  methods: {
    check() {
      // console.log(window.location.pathname.split("/")[1]); = fiends같은 앞머리 가져오는 방법 연구
    },
    changeNow() {
      this.$store.dispatch("chat/changePage", { chat: "chat", roomId: "4" });
    },
  },
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
  padding-right: 50px;
}
.chat-default-container > span {
  padding-bottom: 150px;
  font-size: 25px;
  font-weight: bold;
  color: #90949b;
}
</style>

<script>
export default {
  name: "Setting",
  created() {
    console.log("설정 페이지");
  },
  methods: {
    check() {
      // console.log(window.location.pathname.split("/")[1]); = fiends같은 앞머리 가져오는 방법 연구
    },
  },
};
</script>

<style></style>
