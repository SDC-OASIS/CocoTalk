<template>
  <div class="modal row" @click.self="closeAlert" style="background-color: transparent">
    <div class="modal-container row" style="box-shadow: 10px 10px 30px grey">
      <div class="modal-inner-container" style="text-align: center">
        <div class="text">
          {{ text }}
        </div>
        <div class="row button" style="justify-content: center" @click="closeAlert">
          <Button text="확인" width="80px" height="30px" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Button from "../common/Button.vue";
export default {
  name: "Alert",
  props: {
    text: String,
  },
  data() {
    return {
      friendId: "",
    };
  },
  computed: {
    friendIdCnt() {
      if (this.friendId.length) {
        return this.friendId.length;
      } else {
        return 0;
      }
    },
  },
  components: {
    Button,
  },
  methods: {
    closeAlert() {
      this.$store.dispatch("modal/closeAlert");
    },
  },
};
</script>

<style scoped>
.modal-container {
  width: 400px;
  height: 250px;
  background-color: #ffffff;
  border-radius: 10px;
  position: relative;
  cursor: pointer;
}
.modal-inner-container {
  position: relative;
  padding: 25px 30px;
  justify-content: center;
  width: 350px;
  height: 200px;
}
.text {
  padding-top: 60px;
  font-weight: bold;
  font-size: 20px;
}
.button {
  position: absolute;
  bottom: 30px;
  left: 80px;
}
</style>
