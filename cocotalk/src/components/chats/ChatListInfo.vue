<template>
  <div class="box">
    <div style="font-weight: bold">{{ chatInfo.room.roomname }}</div>
    <div class="chat-list-info-message">{{ chatInfo.recentChatMessage.content }}</div>
  </div>
</template>

<script>
export default {
  name: "ChatListInfo",
  props: {
    chatInfo: Object,
  },
};
</script>

<style scoped>
.box > div {
  padding-left: 10px;
  font-size: 16px;
  text-align: left;
}
.chat-list-info-message {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 160px;
  height: 25px;
  padding-top: 4px;
}
</style>
