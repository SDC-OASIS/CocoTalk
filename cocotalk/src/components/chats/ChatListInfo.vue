<template>
  <div class="box">
    <div class="chat-list-roomname row">
      <div>{{ chatInfo.room.roomname }}</div>
      <div class="member-cnt">{{ chatInfo.room.members.length }}</div>
    </div>
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
  font-size: 14px;
  text-align: left;
}
.chat-list-roomname {
  font-weight: bold;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 140px;
  height: 22px;
}
.chat-list-rommname > div {
  width: 140px;
}
.chat-list-info-message {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 140px;
  height: 20px;
  padding-top: 0px;
}
.member-cnt {
  color: #a2b1ab;
  padding-left: 5px;
  line-height: 20px;
}
</style>
