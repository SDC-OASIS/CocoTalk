<template>
  <div class="button row" :style="style">
    {{ text }}
  </div>
</template>

<script>
export default {
  name: "Button",
  data() {
    return {
      style: Object,
    };
  },
  props: {
    width: {
      type: String,
      default: "40px",
    },
    height: {
      type: String,
      default: "20px",
    },
    text: String,
    backgroundColor: String,
  },
  mounted() {
    this.style = {
      width: this.width,
      height: this.height,
      backgroundColor: this.backgroundColor,
    };
  },
};
</script>

<style>
.button {
  background-color: #fce41e;
  border: 1px solid rgb(230, 207, 6);
  justify-content: center;
  align-items: center;
  padding: 0px 3px;
}
.button:hover {
  background-color: #c9b821 !important;
  border: 1px solid rgb(230, 207, 6);
  cursor: pointer;
}
</style>
