<template>
  <div id="app">
    <router-view name="login" />
    <router-view></router-view>
    <alert v-if="alert.status == 'open'" :text="alert.text" />
  </div>
</template>

<script>
import { mapState } from "vuex";
import Alert from "@/components/modals/Alert.vue";

export default {
  name: "App",
  components: {
    Alert,
  },
  created() {
    // 사용자 화면 넓이 받기
    const width = screen.width;
    this.$store.dispatch("userStore/getScreen", { width: width });
  },
  computed: {
    ...mapState("chat", ["roomStatus"]),
    ...mapState("modal", ["alert"]),
  },
};
</script>

<style>
@import "./css/common.css";
@import "./css/font.css";
</style>
