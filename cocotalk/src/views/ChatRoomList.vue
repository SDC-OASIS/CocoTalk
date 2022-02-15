<template>
  <div class="chat-room-list-outer-container">
    <!-- 상단바 -->
    <div class="header row">
      <span>채팅</span>
      <div class="header-icon-container row">
        <div style="dispaly: inline-block">
          <span class="iconify" data-icon="ant-design:search-outlined" style="color: #aaaaaa"></span>
        </div>
        <div style="dispaly: inline-block" @click="openChatCreationModal">
          <span class="iconify" data-icon="mdi:chat-plus-outline" style="color: #aaaaaa"></span>
        </div>
      </div>
    </div>
    <!-- 채팅방 목록 -->
    <transition-group class="chat-room-list-container" @before-enter="beforeEnter" @after-enter="afterEnter" @enter-cancelled="afterEnter">
      <div class="chat-room-item-container row" v-for="(chat, idx) in chats" :key="chat.room.id" :data-index="idx">
        <div style="dispaly: inline-block; text-align: center">
          <!-- 채팅방 멤버수에 따라 다른 형태의 프로필 모양 -->
          <div v-if="chat.room.members.length == 1">
            <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 50px" />
          </div>
          <div v-if="chat.room.members.length == 2" style="width: 50px; height: 60px">
            <div style="position: absolute">
              <div v-if="!chat.img">
                <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width="30px" :radius="3" />
                <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 30px" class="two-friends-second-img" :radius="3" />
              </div>
              <div v-else>
                <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 30px" />
              </div>
            </div>
          </div>
          <div v-if="chat.room.members.length == 3" style="width: 50px; height: 50px; padding-left: 7px">
            <div style="position: absolute">
              <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 30px" />
              <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 30px" class="three-friends-second-img" :radius="4" />
              <profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 30px" class="three-friends-third-img" :radius="4" />
            </div>
          </div>
          <div v-if="chat.room.members.length >= 4" style="width: 50px; height: 50px; padding-top: 7px">
            <div style="position: absolute">
              <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" class="four-friends-first-img" width=" 25px" />
              <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 25px" class="four-friends-second-img" :radius="5" />
              <profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 25px" class="four-friends-third-img" :radius="5" />
              <profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 25px" class="four-friends-forth-img" :radius="5" />
            </div>
          </div>
        </div>
        <!--채팅방 정보 : 채팅방이름과 마지막 메세지, 안읽은 메세지수 -->
        <div class="chat-info-container row" @click="goChat(chat)">
          <chat-list-info :chatInfo="chat" />
          <div class="box row chat-detail-info">
            <div class="received-time">{{ messageSentTime(chat.recentChatMessage.sentAt) }}</div>
            <div v-show="chat.unreadNumber" class="message-cnt box">{{ chat.unreadNumber }}</div>
          </div>
        </div>
      </div>
    </transition-group>
  </div>
</template>

<script>
import { mapState } from "vuex";
import axios from "@/utils/axios";
import ProfileImg from "@/components/common/ProfileImg.vue";
import ChatListInfo from "@/components/chats/ChatListInfo.vue";

export default {
  name: "ChatList",
  components: {
    ProfileImg,
    ChatListInfo,
  },
  data() {
    return {
      chats: [],
    };
  },
  created() {
    console.log("========[채팅목록페이지]=========");
    this.$store.dispatch("chat/changeMainPage", "chats", { root: true });
    this.getChatList();
    this.chatListSubscribe();
  },
  computed: {
    ...mapState("chat", ["roomStatus"]),
    ...mapState("userStore", ["userInfo"]),
    ...mapState("socket", ["stompChatListClient", "stompChatListConnected", "createChatRoomStatus"]),
  },
  methods: {
    chatListSubscribe() {
      //[채팅목록 마지막 메세지 subscribe]
      this.stompChatListClient.subscribe(`/topic/${this.userInfo.id}/message`, (res) => {
        console.log("구독으로 받은 메시지목록의 마지막 메세지 정보 입니다.");
        const data = JSON.parse(res.body);
        let lastMessage = data.message;
        let bundleInfo = data.bundleInfo;
        const idx = this.chats.findIndex(function (item) {
          return item.room.id == lastMessage.roomId;
        });
        // 1.마지막 메세지 갱신
        this.chats[idx].recentChatMessage = lastMessage;
        // 현재 입장한 방이 아니라면 안읽은 메세지수에 더해줌
        if (this.chats[idx].room.id != this.roomStatus.roomId) {
          this.chats[idx].unreadNumber++;
        }
        // 2.채팅방 목록에 있는 방의 메세지인 경우
        if (idx) {
          const updateData = this.chats[idx];
          console.log(updateData);
          this.chats.splice(idx, 1);
          this.chats.unshift(updateData);
        }
        // 채팅방별 최신 메세지의 번들 수 갱신
        this.chats[idx].recentMessageBundleCount = bundleInfo.currentMessageBundleCount;
        // this.$store.dispatch("chat/updateMessageBundleCount", recentMessageBundleCount, { root: true });
        console.log(lastMessage);

        // 3.채팅방 목록에 없는 방의 메세지인 경우 (나가기한 1대1 채팅) == 구현중 ==
      });

      //[채팅목록 채팅방정보 채널 subscribe] == 구현중 ==
      this.stompChatListClient.subscribe(`/topic/${this.userInfo.id}/room`, (res) => {
        console.log("구독으로 받은 업데이트된 룸정보입니다.");
        console.log(res);
      });

      //[채팅목록 새로 생성된 채팅방정보 채널 subscribe] == 구현중 ==
      this.stompChatListClient.subscribe(`/topic/${this.userInfo.id}/room/new`, (res) => {
        console.log("구독으로 받은 새로 생성된 룸정보입니다.");
        console.log(res);
        console.log("crateChatRoomStatus:");
      });
    },
    getChatList() {
      axios.get("chat/rooms/list").then((res) => {
        console.log("채팅방목록 가져오기");
        const chatList = res.data.data;
        if (chatList) {
          chatList.forEach((e) => {
            e.room.messageBundleIds = e.room.messageBundleIds.slice(1, -1).split(", ");
            e.room.members.forEach((e) => {
              if (e.profile) {
                e.profile = JSON.parse(e.profile);
              }
            });
          });
          this.chats = chatList;
        }
        console.log(chatList);
      });
    },
    openChatCreationModal() {
      this.$store.dispatch("modal/openChatCreationModal", "open", { root: true });
    },
    goChat(chat) {
      // 현재 swagger로 채팅방생성중이기때문에 첫메세지가 없는 경우가 있어 nextMessageBundleId 업데이트.
      // 채팅방내부에서 채팅내역불러와서 갱신해주기 때문에 이후에는 필요없음
      let payload = {
        roomId: chat.room.id,
        nextMessageBundleId: chat.room.messageBundleIds[chat.room.messageBundleIds.length - 1],
        recentMessageBundleCount: chat.recentMessageBundleCount,
      };
      this.$store.dispatch("chat/goChat", payload, { root: true });
      const idx = this.chats.findIndex(function (item) {
        return item.room.id == chat.room.id;
      });
      // 읽지 않은 메세지수 0으로 만들기
      this.chats[idx].unreadNumber = 0;
    },
    messageSentTime(time) {
      return this.$moment(time).format("LT");
    },
    // 트랜지션 시작에서 인덱스*100ms 만큼의 딜레이 부여
    beforeEnter(el) {
      this.$nextTick(() => {
        if (!this.addEnter) {
          // 추가가 아니라면 딜레이 적용
          el.style.transitionDelay = 100 * parseInt(el.dataset.index, 10) + "ms";
        } else {
          // 추가라면 플래그 제거만
          this.addEnter = false;
        }
      });
    },
    // 트랜지션을 완료하거나 취소할 때는 딜레이를 제거합니다.
    afterEnter(el) {
      el.style.transitionDelay = "";
    },
    // 목록에서 삭제시 애니메이션을 보기위한 테스트 함수입니다.
    deleteMessage(id) {
      console.log(id);
      const idx = this.chats.findIndex(function (item) {
        return item.room.id == id;
      });
      console.log(idx);
      this.chats.splice(idx, 1);
      console.log(this.chats);
    },
  },
};
</script>
<style scoped>
.chat-room-list-outer-container {
  display: block;
  padding-top: 20px;
  background-color: #ffffff;
  border-left: 2px solid #9eac95;
  border-right: 2px solid #9eac95;
  font-size: 15px;
}
.header {
  justify-content: space-between;
}
.header-icon-container {
  width: 75px;
  justify-content: space-between;
  align-items: center;
  padding-right: 20px;
}
.header > span {
  color: #749f58;
  font-size: 25px;
  font-weight: bold;
  padding-left: 20px;
}
.header div {
  font-size: 23px;
  font-weight: bold;
  /* margin-right: 20px; */
}
.iconify {
  padding: 7px;
  border-radius: 15px;
}
.iconify:hover {
  background-color: #e7f7dd;
  cursor: pointer;
}

.chat-room-list-container {
  padding: 20px 0;
  text-align: left;
  display: block;
  height: 82vh;
  overflow: auto;
}
.chat-room-list-container::-webkit-scrollbar {
  background-color: #ffffff;
  width: 18px;
}
.chat-room-list-container::-webkit-scrollbar-track {
  background-color: #ffffff;
  width: 10px;
}
.chat-room-list-container::-webkit-scrollbar-thumb {
  background-color: #b8c8ae;
  border-radius: 10px;
  width: 10px;
  background-clip: padding-box;
  border: 5px solid transparent;
}

.chat-room-list-container > span {
  margin-bottom: 100px;
}
.chat-room-item-container {
  padding: 10px 0;
  align-items: center;
  padding-left: 20px;
  cursor: pointer;
}

.chat-room-item-container img {
  width: 50px;
  height: 50px;
}
.chat-room-item-container:hover {
  background-color: #e7f7dd;
}
.chat-info-container {
  justify-content: space-between;
  width: 80%;
}
.received-time {
  color: #90949b;
  font-size: 13px;
  padding-top: 4px;
}
.message-cnt {
  background-color: #e80c4e;
  color: #ffffff;
  text-align: right;
  padding: 3px 7px;
  font-weight: bold;
  border-radius: 20px;
  margin-top: 5px;
}
.chat-detail-info {
  text-align: right;
}

@media (max-width: 1600px) {
  .chat-info-container {
    justify-content: space-between;
    width: 75%;
  }
}
.two-friends-second-img {
  position: relative;
  top: -20px;
  left: -14px;
}

.three-friends-second-img {
  position: relative;
  top: -15px;
  left: -45px;
}
.three-friends-third-img {
  position: relative;
  top: -15px;
  left: -20px;
}
.four-friends-first-img {
  position: relative;
  top: -5px;
  left: -3px;
  z-index: 3;
}
.four-friends-second-img {
  position: relative;
  top: -35px;
  left: -6px;
  z-index: 3;
}
.four-friends-third-img {
  position: relative;
  top: -8px;
  left: -34px;
}
.four-friends-forth-img {
  position: relative;
  top: -8px;
  left: -7px;
}
/* 트랜지션 전용 스타일 */
.v-enter-active,
.v-leave-active,
.v-move {
  transition: opacity 0.5s, transform 0.5s;
}
.v-leave-active {
  position: absolute;
}

.v-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
