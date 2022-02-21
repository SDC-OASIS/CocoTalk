<template>
  <div class="box">
    <div style="font-weight: bold; font-size: 14px">{{ userInfo.username }}</div>
    <div style="font-size: 14px; padding-top: 3px">{{ userInfo.profile.message }}</div>
  </div>
</template>

<script>
export default {
  name: "FriendListUserInfo",
  props: {
    userInfo: Object,
  },
};
</script>

<style scoped>
.box > div {
  padding-left: 15px;
  font-size: 16px;
  text-align: left;
}
</style>
