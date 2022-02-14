<template>
  <div class="chat">
    <!-- 채팅방 Header -->
    <div class="chat-header row align-center">
      <div class="row align-center">
        <div @click="exitChat">
          <span class="iconify exit-chat" data-icon="eva:arrow-ios-back-outline"></span>
        </div>
        <span class="bold" style="font-size: 18px">999+</span>
      </div>
      <span class="bold" style="font-size: 18px">{{ this.roomInfo.roomname }}</span>
      <div class="box">
        <span>
          <span class="iconify" data-icon="ant-design:search-outlined" style="cursor: pointer; color: black; padding-right: 10px"></span>
        </span>
        <span @click="openSidebar">
          <span class="iconify" data-icon="charm:menu-hamburger" style="color: black; cursor: pointer"></span>
        </span>
      </div>
    </div>
    <!-- 채팅 대화 -->
    <div class="chat-messages-outer-container" id="chatMessagesContainer" ref="scrollRef" @scroll="handleScroll">
      <div class="chat-messages-container">
        <!-- <infinite-loading v-if="limit" direction="top" @infinite="infiniteHandler" spinner="waveDots"></infinite-loading> -->
        <div class="chat-messages" v-for="(chatMessage, idx) in chatMessages" :key="idx">
          <!-- 상대가 한 말 -->
          <div v-if="chatMessage.userId != userInfo.id" class="row">
            <div @click="openProfileModal(getMemberInfo(chatMessage.userId))" style="cursor: pointer">
              <ProfileImg :imgUrl="profileImg(chatMessage.userId)" width="40px" />
            </div>
            <div class="chat-message">
              <div style="padding-bottom: 7px">{{ sentUserName(chatMessage.userId) }}</div>
              <div class="row">
                <div class="bubble box">{{ chatMessage.content }}</div>
                <div style="position: relative; width: 70px">
                  <div class="unread-number">2</div>
                  <div class="sent-time">오후2:00</div>
                </div>
              </div>
            </div>
          </div>
          <!-- 내가 한 말 -->
          <div v-else class="row" style="justify-content: right">
            <div class="chat-message">
              <div class="row">
                <div style="position: relative; width: 55px">
                  <div class="unread-number-me">2</div>
                  <div class="sent-time-me">오후2:00</div>
                </div>
                <div class="bubble-me box">{{ chatMessage.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="message-input-container row">
      <!-- <input v-model.trim="message" type="textarea" @keypress.enter="send" /> -->
      <textarea v-model.trim="message" @keypress.enter="send"></textarea>
      <div @click="send">
        <Button text="전송" width="50px" height="30px" style="margin-top: 15px; margin-left: 16px" />
      </div>
    </div>
    <Sidebar />
  </div>
</template>

<script>
import { mapMutations, mapState } from "vuex";
import ProfileImg from "../components/common/ProfileImg.vue";
import Button from "../components/common/Button.vue";
import Sidebar from "../components/chatroom/Sidebar.vue";
// import InfiniteLoading from "vue-infinite-loading";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import axios from "@/utils/axios";

export default {
  data() {
    return {
      roomId: this.$route.params.roomId,
      roomMemberIds: [],
      chatMessages: [],
      roomInfo: {},
      message: "",
      limit: 0,
      previousTop: 0,
      previousBottom: 0,
      moreMessages: 0,
    };
  },
  components: {
    // InfiniteLoading,
    ProfileImg,
    Button,
    Sidebar,
  },
  created() {
    console.log("===========[채팅페이지]============");
    this.$store.dispatch(
      "chat/changePage",
      {
        mainPage: this.roomStatus.mainPage,
        roomId: this.$route.params.roomId,
      },
      { root: true },
    );
    this.chatRoomConnect();
    this.getChat();
    this.$nextTick(() => {
      let chatMessages = this.$refs.scrollRef;
      chatMessages.scrollTo({ top: chatMessages.scrollHeight - chatMessages.clientHeight, behavior: "smooth" });
    });
  },
  mounted: function () {
    // window.addEventListener("scroll", this.handleNotificationListScroll);
  },
  computed: {
    ...mapState("chat", ["roomStatus", "friends", "chattings", "chatInfo"]),
    ...mapState("userStore", ["userInfo"]),
    ...mapState("socket", ["stompChatRoomClient", "stompChatRoomConnected"]),
  },
  watch: {
    "$route.params.roomId": function () {
      const headers = {
        action: "leave",
      };
      this.stompChatRoomClient.disconnect(() => {}, headers);
      // vuex에 마지막 페이지 방문 저장
      this.$store.dispatch(
        "chat/changePage",
        {
          mainPage: this.roomStatus.mainPage,
          chat: this.$route.params.chat,
          roomId: this.$route.params.roomId,
        },
        { root: true },
      );
      this.chatMessages = [];
      this.getChat();
      this.chatRoomConnect();
    },
    // 스크롤 메세지리스트 최하단으로 이동
  },
  methods: {
    ...mapMutations("socket", ["setStompChatRoomClient", "setStompChatRoomConnected"]),

    // 1.채팅내역 불러오기
    getChat() {
      this.roomMemberIds = [];
      axios.get(`chat/rooms/${this.roomStatus.roomId}/tail?count=${this.chatInfo.recentMessageBundleCount}`).then((res) => {
        console.log("채팅내역 가져오기");
        let chatData = res.data.data;
        this.roomInfo = chatData.room;
        this.chatMessages = chatData.messageList;
        chatData.room.messageBundleIds = chatData.room.messageBundleIds.slice(1, -1).split(", ");
        // 새로 받은 최신 bundleId 업데이트
        const payload = {
          nextMessageBundleId: this.roomInfo.messageBundleIds[this.roomInfo.messageBundleIds.length - 1],
        };
        this.$store.dispatch("chat/updateMessageBundleId", payload, { root: true });
        // 이후 메세지 보낼때 필요한 룸멤버의 아이디 값 계산
        this.roomInfo.members.forEach((e) => {
          this.roomMemberIds.push(e.userId);
          if (e.profile) {
            e.profile = JSON.parse(e.profile);
          }
        });
        console.log(this.chatMessages);
        console.log(this.roomInfo);
        this.limit += 1;
        let chatMessages = this.$refs.scrollRef;
        console.log(chatMessages.scrollHeight - chatMessages.clientHeight);
        chatMessages.scrollTo({ top: chatMessages.scrollHeight - chatMessages.clientHeight, behavior: "smooth" });
      });
    },

    // 2.채팅방에 참여중인 멤버의 정보를 메세지마다 적용해줍니다.
    sentUserName(userId) {
      const idx = this.roomInfo.members.findIndex(function (item) {
        return item.userId == userId;
      });
      return this.roomInfo.members[idx].username;
    },
    profileImg(userId) {
      const idx = this.roomInfo.members.findIndex(function (item) {
        return item.userId == userId;
      });
      return this.roomInfo.members[idx].profile.profile;
    },
    getMemberInfo(userId) {
      const idx = this.roomInfo.members.findIndex(function (item) {
        return item.userId == userId;
      });
      return this.roomInfo.members[idx];
    },
    openProfileModal(userProfileInfo) {
      this.$store.dispatch("modal/openProfileModal", { status: "open", userProfileInfo: userProfileInfo }, { root: true });
    },

    // 3.채팅방 소켓 연결
    chatRoomConnect: function () {
      const serverURL = "http://138.2.93.111:8080/stomp";
      let socket = new SockJS(serverURL);
      this.setStompChatRoomClient(Stomp.over(socket));
      this.stompChatRoomClient.connect(
        { view: "chatRoom", userId: this.userInfo.id, roomId: this.roomStatus.roomId },
        (frame) => {
          // 소켓 연결 성공
          this.connected = true;
          console.log("소켓 연결 성공", frame);
          // 채팅 메세지 채널 subscribe
          this.stompChatRoomClient.subscribe(`/topic/${this.roomStatus.roomId}/message`, (res) => {
            console.log("구독으로 받은 메시지 입니다.");
            const receivedMessage = JSON.parse(res.body);
            this.chatMessages.push(receivedMessage.message);
            console.log(receivedMessage);
            // 새로 메세지가 들어올 경우 마지막 메세지의 BundleId로 업데이트
            const payload = {
              nextMessageBundleId: receivedMessage.bundleInfo.nextMessageBundleId,
            };
            this.$store.dispatch("chat/updateMessageBundleId", payload, { root: true });
          });
          // 채팅 메세지 룸정보 업데이트 채널 subscribe
          this.stompChatRoomClient.subscribe(`/topic/${this.roomStatus.roomId}/room`, (res) => {
            console.log("구독으로 받은 채팅방 정보입니다.");
            this.roomInfo = JSON.parse(res.body);
            this.roomInfo.members.forEach((e) => {
              this.roomMemberIds = [];
              this.roomMemberIds.push(e.userId);
              if (e.profile) {
                e.profile = JSON.parse(e.profile);
              }
            });
            console.log(this.roomInfo);
          });
          // 채팅방 초대 - 이전의 Join과 다름. 좀 더 생각해보기
          // const msg = {
          // 	type: 3,
          // 	roomId: this.roomStatus.roomId,
          // 	userId: this.userInfo.id,
          // 	inviteIds: [10],
          // 	messageBundleId: this.socket.recentMessageBundleId,
          // 	content: "초대메세지",
          // };
          // console.log("초대");
          // this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/invite`, JSON.stringify(msg), {});
        },
        (error) => {
          // 소켓 연결 실패
          console.log("소켓 연결 실패", error);
          this.connected = false;
        },
      );
      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
    },
    // 메세지 전송 클릭시 소켓이 연결되어 있고 입력한 메세지가 있다면 전송합니다.
    send() {
      if (this.stompChatRoomClient && this.stompChatRoomClient.connected && this.message) {
        const msg = {
          type: 0,
          content: this.message,
          roomId: this.roomStatus.roomId,
          roomType: this.roomInfo.type,
          roomname: this.roomInfo.roomname,
          userId: this.userInfo.id,
          username: this.userInfo.username,
          receiverIds: this.roomMemberIds,
          messageBundleId: this.chatInfo.nextMessageBundleId,
        };
        this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/send`, JSON.stringify(msg));
      }
      this.message = "";
      // [메세지보낸 후 엔터커서 위로 올라오지 않는 현상 해결중]
      // e.target.value = "";
      // document.getElementById("textarea").focus();
    },

    openSidebar() {
      const sidebar = document.querySelector(".sidebar-container");
      const sidebarBack = document.querySelector(".sidebar-background");
      sidebarBack.style.display = "block";
      sidebar.style.right = "0px";
    },
    exitChat() {
      const headers = {
        action: "leave",
      };
      this.stompChatRoomClient.disconnect(() => {}, headers);
      this.$store.dispatch("chat/changePage", { mainPage: this.roomStatus.mainPage, chat: "chat", roomId: false }, { root: true });
    },

    //==============무한스크롤 구현중입니다.========================

    handleScroll(e) {
      const { scrollHeight, scrollTop, clientHeight } = e.target;
      console.log(clientHeight, scrollHeight, scrollTop);

      if (scrollTop & !this.moreMessages) {
        this.$nextTick(() => {
          let chatMessages = this.$refs.scrollRef;
          chatMessages.scrollTo({ top: scrollHeight - clientHeight, behavior: "smooth" });
        });
        // this.$refs.scrollRef.screenTo(scrollHeight - clientHeight);
      }

      if (scrollTop > this.previousTop) {
        this.previousTop = scrollTop;
      } else if (scrollTop == this.previousTop) {
        this.$nextTick(() => {
          let chatMessages = this.$refs.scrollRef;
          chatMessages.scrollTo({ top: chatMessages.scrollHeight, behavior: "smooth" });
        });
      }

      if (!scrollTop & this.moreMessages) {
        this.infiniteHandler(e);
        this.$nextTick(() => {
          // this.loadingIsActive = false;
        });
      }
    },

    infiniteHandler() {
      console.log("!!!!");
      // console.log($state);
      axios
        .get("chat/messages", {
          params: {
            roomId: this.roomStatus.roomId,
            bundleId: this.chatMessages[0].messageBundleId,
            count: this.chatInfo.recentMessageBundleCount - 20 + 20,
            size: 20,
          },
        })
        .then((res) => {
          console.log("스크롤업");
          console.log(res);

          if (res.data.data.length) {
            // this.limit += 1;
            console.log(res.data.data);
            this.chatMessages.unshift(...res.data.data);
            console.log(this.chatMessages);
            this.$nextTick(() => {
              this.loadingIsActive = false;
            });
            // $state.loaded();
            // $state.complete();
          } else {
            // $state.complete();
          }
        });
    },
    // scroll: function (e) {
    //   this.scrollPostion = e.target.scrollTop;
    //   if (this.scrollPosition > 100) {
    //     console.log("UP");
    //   } else {
    //     console.log("DOWN");
    //   }
    // },
  },
};
</script>

<style scoped>
.chat-header {
  justify-content: space-between;
  padding: 20px;
}
.chat-header .iconify {
  font-size: 25px;
  font-weight: bold;
}
.exit-chat {
  cursor: pointer;
}
.chat-messages-outer-container {
  padding: 0px 30px;
  overflow: auto;
}
.chat-messages-container {
  height: 72vh;
}
@media (max-height: 880px) {
  .chat-messages-container {
    height: 70vh;
  }
}
.chat-messages-outer-container::-webkit-scrollbar {
  position: absolute;
  background-color: #d8eec0;
  width: 23px;
}
.chat-messages-outer-container::-webkit-scrollbar-track {
  background-color: #d8eec0;
  width: 10px;
}
.chat-messages-outer-container::-webkit-scrollbar-thumb {
  background-color: #b8c8ae;
  border-radius: 10px;
  width: 10px;
  background-clip: padding-box;
  border: 8px solid transparent;
}
.chat-messages {
  text-align: left;
  padding: 10px 0;
  font-size: 14px;
}
.chat-message {
  padding-left: 15px;
}
.chat {
  background-color: #d8eec0;
  border-right: 2px solid #9eac95;
  position: relative;
}
.bubble {
  position: relative;
  padding: 7px 10px;
  background: #ffffff;
  border-radius: 5px;
  margin-right: 5px;
  max-width: 250px;
  word-break: break-all;
}
.bubble:after {
  content: "";
  position: absolute;
  border-style: solid;
  border-width: 0px 20px 15px 0;
  border-color: transparent #ffffff;
  display: block;
  width: 0;
  z-index: 1;
  left: -11px;
  top: 8px;
}
.unread-number {
  color: #749f58;
  font-size: 10px;
  position: absolute;
  bottom: 10px;
  left: 1px;
  width: 70px;
  height: 15px;
}
.unread-number-me {
  color: #749f58;
  font-size: 10px;
  position: absolute;
  bottom: 11px;
  right: 1px;
  width: 12px;
  height: 15px;
}
.sent-time {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 70px;
  height: 15px;
  font-size: 11px;
}
.sent-time-me {
  position: absolute;
  bottom: 2px;
  right: 0;
  width: 50px;
  height: 15px;
  font-size: 11px;
}

.bubble-me {
  position: relative;
  padding: 7px 10px;
  background: #ffed59;
  border-radius: 5px;
  max-width: 300px;
  word-break: break-all;
}
.bubble-me:after {
  content: "";
  position: absolute;
  border-style: solid;
  border-width: 0px 0px 15px 20px;
  border-color: transparent #ffed59;
  display: block;
  width: 0;
  z-index: 1;
  right: -11px;
  top: 8px;
}
.message-input-container {
  position: fixed;
  background-color: #ffffff;
  height: 15vh;
  bottom: 0px;
  width: inherit;
  text-align: left;
  z-index: 2;
}
.message-input-container textarea {
  width: 80%;
  margin: 15px 0;
  padding: 10px 10px;
  outline: none;
  border: none;
  resize: none;
  font-size: 20px;
}
</style>
