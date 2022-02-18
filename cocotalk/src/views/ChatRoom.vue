<template>
  <div v-if="this.stompChatRoomConnected" class="chat">
    <!-- 채팅방 Header -->
    <div class="chat-header row align-center">
      <div class="row align-center">
        <div @click="exitChat">
          <span class="iconify exit-chat" data-icon="eva:arrow-ios-back-outline"></span>
        </div>
        <span class="bold" style="font-size: 18px">{{ totalUnreadMessageCnt }}</span>
      </div>
      <span v-if="this.roomInfo.type == 0" class="bold chat-room-title" style="font-size: 18px">{{ privateRoomname }}</span>
      <span v-else class="bold chat-room-title" style="font-size: 18px">{{ this.roomInfo.roomname }}</span>
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
        <!-- <infinite-loading v-if="limit" direction="top" @infinite="getMessageHistory" spinner="waveDots"></infinite-loading> -->
        <div class="chat-messages" v-for="(chatMessage, idx) in chatMessages" :key="idx">
          <div v-if="chatMessage.type == 0">
            <!-- 상대방이 보낸 메시지 -->
            <div v-if="chatMessage.userId != userInfo.id" class="row">
              <div @click="openProfileModal(getMemberInfo(chatMessage.userId))" style="cursor: pointer">
                <ProfileImg :imgUrl="profileImg(chatMessage.userId)" width="40px" />
              </div>
              <div class="chat-message">
                <div style="padding-bottom: 7px">{{ sentUserName(chatMessage.userId) }}</div>
                <div class="row">
                  <div class="bubble box">{{ chatMessage.content }}</div>
                  <div style="position: relative; width: 70px">
                    <div class="unread-number">{{ unreadMemberCnt(chatMessage.sentAt) }}</div>
                    <div class="sent-time">{{ messageSentTime(chatMessage.sentAt) }}</div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 내가 보낸 메시지 -->
            <div v-else class="row" style="justify-content: right">
              <div class="chat-message">
                <div class="row">
                  <div style="position: relative; width: 55px">
                    <div class="unread-number-me">{{ unreadMemberCnt(chatMessage.sentAt) }}</div>
                    <div class="sent-time-me">{{ messageSentTime(chatMessage.sentAt) }}</div>
                  </div>
                  <div class="bubble-me box">{{ chatMessage.content }}</div>
                </div>
              </div>
            </div>
          </div>
          <!-- 초대 메시지 -->
          <div v-else-if="chatMessage.type == 1" class="invite-message">
            <div>{{ chatMessage.content }}</div>
          </div>
          <!-- 퇴장 메시지 -->
          <div v-else-if="roomInfo.type != 0 && chatMessage.type == 2" class="invite-message">
            <div>{{ chatMessage.content }}</div>
          </div>
          <!-- 사진 메시지 -->
          <div v-else-if="chatMessage.type == 4" :class="{ 'my-file-message': chatMessage.userId == userInfo.id }">
            <div>
              <div v-if="chatMessage.userId == userInfo.id" class="my-file-message-info">
                <div class="unread-number-me">{{ unreadMemberCnt(chatMessage.sentAt) }}</div>
                <div class="sent-time-me">{{ messageSentTime(chatMessage.sentAt) }}</div>
              </div>
              <img @click="openClick(chatMessage.content)" class="img-message" :src="chatMessage.content" />
              <div v-if="chatMessage.userId != userInfo.id" class="others-file-message-info">
                <div class="sent-time-me">{{ messageSentTime(chatMessage.sentAt) }}</div>
                <div class="unread-number" style="left: 6px; margin-bottom: 2px">{{ unreadMemberCnt(chatMessage.sentAt) }}</div>
              </div>
            </div>
          </div>
          <!-- 동영상 메시지 -->
          <div v-else-if="chatMessage.type == 5" :class="{ 'my-file-message': chatMessage.userId == userInfo.id }">
            <div>
              <div v-if="chatMessage.userId == userInfo.id" class="my-file-message-info">
                <div class="unread-number-me">{{ unreadMemberCnt(chatMessage.sentAt) }}</div>
                <div class="sent-time-me">{{ messageSentTime(chatMessage.sentAt) }}</div>
              </div>
              <video controls class="video-message" :src="chatMessage.content">해당 브라우저에서 지원하지 않습니다</video>
              <!-- <video preload="metadata" :src="chatMessage.content | videoThumbNail"></video> -->
              <!-- <img @click="openClick(chatMessage.content)" class="img-message" :src="chatMessage.content" /> -->
              <div v-if="chatMessage.userId != userInfo.id" class="others-file-message-info">
                <div class="sent-time-me">{{ messageSentTime(chatMessage.sentAt) }}</div>
                <div class="unread-number" style="left: 6px; margin-bottom: 2px">{{ unreadMemberCnt(chatMessage.sentAt) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="message-input-container row">
      <!-- 파일 업로드 로딩 -->
      <div v-if="isLoading" class="loading">
        <div>파일이 업로드 중입니다...</div>
        <img src="@/assets/spinner-green.gif" />
      </div>
      <!-- <input v-model.trim="message" type="textarea" @keypress.enter="send" /> -->
      <textarea v-model.trim="message" @keypress.enter.prevent="send(0, message)"></textarea>
      <div @click="send(0, message)">
        <Button text="전송" width="50px" height="30px" style="margin-top: 15px; margin-left: 16px" />
      </div>
      <!-- 파일 업로드 버튼 START -->
      <div class="image-upload">
        <label for="file-input">
          <div>
            <span class="iconify" data-icon="ant-design:paper-clip-outlined"></span>
          </div>
        </label>
        <input id="file-input" type="file" @change="handleFileChange" />
      </div>
      <!-- 파일 업로드 버튼 END -->
    </div>
    <Sidebar v-if="sidebar" :roomInfo="roomInfo" />
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
      previousScrollHeight: 0,
      moreMessages: 0,
      nowScroll: 0,
      bottomScrollTop: 0,
      isLoading: false,
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
    this.$store.dispatch("chat/changePage", { mainPage: this.roomStatus.mainPage, roomId: this.$route.params.roomId }, { root: true });
    this.$store.dispatch("modal/setSidebar", true, { root: true });

    if (this.newPrivateRoomStatus && !this.triggerMessage) {
      console.log("============[개인톡방생성페이지]=============");
      this.newPrivateRoom();
    } else {
      console.log("=====커넥션을 시작합니다=====");
      this.chatRoomConnect();
      if (!this.createChatRoomStatus && !this.triggerMessage) {
        this.getChat();
      }
    }
  },
  computed: {
    ...mapState("chat", ["roomStatus", "friends", "chattings", "chatInfo", "newRoomInfo"]),
    ...mapState("userStore", ["userInfo"]),
    ...mapState("modal", ["roomNameEditModal", "sidebar"]),
    ...mapState("socket", [
      "stompChatRoomClient",
      "stompChatRoomConnected",
      "createChatRoomStatus",
      "newPrivateRoomStatus",
      "newPrivateRoomFriendInfo",
      "triggerMessage",
      "chats",
      "newPrivateRoomRefresh",
      "awakePrivateRoomStatus",
      "awakePrivateRoomInfo",
    ]),
    totalUnreadMessageCnt() {
      let cnt = 0;
      this.chats.forEach((e) => {
        cnt += e.unreadNumber;
      });
      return cnt;
    },
    privateRoomname() {
      let roomname;
      this.roomInfo.members.forEach((e) => {
        if (e.userId != this.userInfo.id) {
          roomname = e.username;
        }
      });
      return roomname;
    },
  },
  watch: {
    // 채팅방을 켜둔상태에서 다른 채팅방으로 이동할 경우
    "$route.params.roomId": function () {
      // 이전 채팅방 disconnect
      this.$store.dispatch("modal/setSidebar", true, { root: true });
      this.setAwakePrivateRoomStatus(false);
      this.setAwakePrivateRoomInfo(null);

      if (!this.newPrivateRoomStatus) {
        const headers = { action: "leave" };
        console.log("이전 방 나가기");
        this.stompChatRoomClient.disconnect(() => {}, headers);
      }
      // vuex에 이동한 url저장
      this.$store.dispatch(
        "chat/changePage",
        {
          mainPage: this.roomStatus.mainPage,
          chat: this.$route.params.chat,
          roomId: this.$route.params.roomId,
        },
        { root: true },
      );
      this.moreMessages = 0; //채팅방 히스트로리 불러올 때 = true
      this.chatMessages = [];
      if (this.newPrivateRoomStatus && !this.triggerMessage) {
        console.log("============[개인톡방생성페이지]=============");
        this.newPrivateRoom();
      } else {
        console.log("=====커넥션을 시작합니다=====");
        this.chatRoomConnect();
      }
      // 스크롤 최하단으로 이동
      this.$nextTick(() => {
        let chatMessages = this.$refs.scrollRef;
        chatMessages.scrollTo({ top: chatMessages.scrollHeight });
        this.previousScrollHeight = chatMessages.scrollHeight; //메세지 불러온 후 scrollHeight
        this.bottomScrollTop = chatMessages.scrollTop; //메세지 불러온 후 최하단 scrollTop
        // 처음입장후 하단에 스크롤 배치가 끝났으므로 이후 최상단 스크롤은 요청
        this.$el.addEventListener("scroll", this.handleScroll);
      });
    },
    chatMessages() {
      let chatMessages = this.$refs.scrollRef;
      this.previousScrollHeight = chatMessages.scrollHeight;
      this.bottomScrollTop = chatMessages.scrollTop;
    },
  },
  destroyed() {
    this.setAwakePrivateRoomStatus(false);
    this.setAwakePrivateRoomInfo(null);
  },
  methods: {
    ...mapMutations("socket", [
      "setStompChatRoomClient",
      "setStompChatRoomConnected",
      "setCreateChatRoomStatus",
      "setNewPrivateRoomStatus",
      "setNewPrivateRoomRefresh",
      "setInviteRoomInfo",
      "setAwakePrivateRoomStatus",
      "setAwakePrivateRoomInfo",
    ]),
    // 이미지 클릭했을 때 원본 이미지로 보여주기
    openClick(url) {
      var img = new Image();
      img.src = url;
      var img_width = img.width;
      var win_width = img.width + 25;
      var img_height = img.height;
      var curX = window.screenLeft;
      var curY = window.screenTop;
      var curWidth = document.body.clientWidth;
      var curHeight = document.body.clientHeight;

      var nLeft = curX + curWidth / 2 - img_width / 2;
      var nTop = curY + curHeight / 2 - img_height / 2;

      var strOption = "";
      strOption += "left=" + nLeft + "px,";
      strOption += "top=" + nTop + "px,";
      strOption += "width=" + img_width + "px,";
      strOption += "height=" + img_height + "px,";
      strOption += "toolbar=no,menubar=no,location=no,scrollbars=yes,";
      strOption += "resizable=yes,status=no";
      var OpenWindow = window.open("/", "popup", strOption);
      // var OpenWindow = window.open("/", "popup", `width=${img_width}, height=${img_height}, left=${left}, top=${tops}`);
      OpenWindow.document.write(`<style>body{margin:0px;}</style><img src='${url}' width='$${win_width}'>`);
    },
    // 1.채팅내역 불러오기
    // 1-1. 일반 채팅방 입장
    getChat() {
      this.roomMemberIds = [];
      // 본인이 나간 개인톡방인경우
      if (this.awakePrivateRoomStatus) {
        console.log("내가이미나간 톡방");
        console.log(this.awakePrivateRoomInfo);
        this.roomInfo = this.awakePrivateRoomInfo;
        return;
      }
      axios.get(`chat/rooms/${this.roomStatus.roomId}/tail?count=${this.chatInfo.recentMessageBundleCount}`).then((res) => {
        console.log("채팅내역 가져오기");
        console.log(this.chatInfo.recentMessageBundleCount);
        let chatData = res.data.data;
        if (chatData) {
          if (this.triggerMessage) {
            this.sendTriggerMessage();
          }
          this.roomInfo = chatData.room;
          this.setInviteRoomInfo(this.roomInfo);

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
          this.moreMessages = 1; // 첫입장 후 스크롤 최상단 위치시 채팅내역 불러올 수 있게 변경
          this.$nextTick(() => {
            let chatMessages = this.$refs.scrollRef;
            chatMessages.scrollTo({ top: chatMessages.scrollHeight });
            this.previousScrollHeight = chatMessages.scrollHeight;
            this.bottomScrollTop = chatMessages.scrollTop;
            // 처음입장후 하단에 스크롤 배치가 끝났으므로 이후 최상단 스크롤은 요청
            // this.moreMessages = 1;
            this.$el.addEventListener("scroll", this.handleScroll);
          });
        }
      });
    },
    // 1-2. 채팅방 생성시 자동 입장
    getNewChat() {
      console.log("========[새로운 채팅방 입성]==========");
      this.sendInviteMessage();
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
    messageSentTime(time) {
      return this.$moment(time).format("LT");
    },
    unreadMemberCnt(sentAt) {
      // console.log("=======카톡안읽은 사람 숫자 연산 =========");
      if (this.roomInfo.members) {
        let cnt = this.roomInfo.members.length;
        this.roomInfo.members.forEach((e) => {
          // console.log(e.username + "접속시간:" + e.enteredAt + "나간시간:" + e.awayAt);
          // 현재접속중인 사람은 읽음
          if (e.enteredAt > e.awayAt) {
            cnt = cnt - 1;
          }
          // 미접속자중 메세지가 온때보다 늦게나간 사람은 읽음
          else if (e.awayAt > sentAt) {
            cnt = cnt - 1;
          }
        });
        if (cnt) {
          return cnt;
        }
      }
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
          if (!this.createChatRoomStatus && !this.triggerMessage) {
            this.getChat();
          }
          if (this.createChatRoomStatus) {
            this.getNewChat();
          }
          if (this.triggerMessage) {
            this.sendTriggerMessage();
          }
          this.setStompChatRoomConnected(true);
          // 채팅 메세지 채널 subscribe
          this.stompChatRoomClient.subscribe(`/topic/${this.roomStatus.roomId}/message`, (res) => {
            console.log("구독으로 받은 메시지 입니다.");
            console.log("현재갠톡방상성상태:" + "개인톡방생성중:" + this.newPrivateRoomStatus + "단톡생성중:" + this.createChatRoomStatus);
            const receivedMessage = JSON.parse(res.body);
            this.chatMessages.push(receivedMessage.message);
            console.log(receivedMessage);
            // 새로 메세지가 들어올 경우 마지막 메세지의 BundleId로 업데이트 / 채팅방 첫 생성시를 위한 bundlecount도 업데이트
            const payload = {
              nextMessageBundleId: receivedMessage.bundleInfo.nextMessageBundleId,
            };
            this.$store.dispatch("chat/updateMessageBundleId", payload, { root: true });
            this.$store.dispatch("chat/updateMessageBundleCount", receivedMessage.bundleInfo.currentMessageBundleCount, { root: true });
            let scrollBottom = this.scrollBottom;
            setTimeout(() => scrollBottom(), 100);
            // 만약 처음 생성된방이라면
            if (this.newPrivateRoomRefresh) {
              this.getChat();
              console.log("처음생성된 방:" + this.newPrivateRoomRefresh);
              this.setNewPrivateRoomRefresh(false);
            }
          });
          // 채팅 메세지 룸정보 업데이트 채널 subscribe
          this.stompChatRoomClient.subscribe(`/topic/${this.roomStatus.roomId}/room`, (res) => {
            console.log("구독으로 받은 채팅방 정보입니다");
            console.log(JSON.parse(res.body));
            this.roomInfo = JSON.parse(res.body);
            this.roomInfo.members.forEach((e) => {
              this.roomMemberIds = [];
              this.roomMemberIds.push(e.userId);
              if (e.profile) {
                e.profile = JSON.parse(e.profile);
              }
            });
          });
        },
        (error) => {
          // 소켓 연결 실패
          console.log("소켓 연결 실패", error);
          this.connected = false;
        },
      );
      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
    },
    // 메세지를 전송합니다.
    send(messageType, messageContent) {
      if (this.newPrivateRoomStatus) {
        this.sendToCreatePrivateRoom();
      } else {
        // 빈 채팅은 보내지 않습니다.
        if (messageType == 0 && (!this.message || this.message.length == 0)) return;
        // 소켓이 연결되어 있어야 전송합니다.
        if (!this.stompChatRoomClient || !this.stompChatRoomClient.connected) return;

        // awake message를 보내는경우 (상대가 나간 개인톡방)
        let joining = "";
        if (this.roomInfo.type == 0) {
          this.roomInfo.members.forEach((e) => {
            if (e.joining == false) {
              messageType = 3;
              this.sendToAwake(messageContent);
              // this.getChat();
              return;
            }
          });
          console.log("상대방:" + joining);
        }
        // if (!joining) {
        //   messageType = 3;
        //   this.sendToAwake(messageContent);
        //   return;
        // }
        // awake message를 보내는경우 (내가 나간 개인톡방)

        // this.sendToAwakeOut();

        const msg = {
          type: messageType,
          content: messageContent,
          roomId: this.roomStatus.roomId,
          roomType: this.roomInfo.type,
          roomname: this.roomInfo.roomname,
          userId: this.userInfo.id,
          username: this.userInfo.username,
          receiverIds: this.roomMemberIds,
          messageBundleId: this.chatInfo.nextMessageBundleId,
        };
        this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/send`, JSON.stringify(msg));

        this.message = "";
      }
    },
    sendToAwake(messageContent) {
      let messageBundleId = this.chatInfo.nextMessageBundleId;
      // 내가 나간 개인톡방인경우
      if (this.awakePrivateRoomInfo) {
        messageBundleId = this.roomInfo.messageBundleIds[this.roomInfo.messageBundleIds.length - 1];
      }
      let roomMemberIds = [];
      this.roomInfo.members.forEach((e) => {
        roomMemberIds.push(e.userId);
      });

      const msg = {
        type: 3,
        content: messageContent,
        roomId: this.roomStatus.roomId,
        roomType: 0,
        roomname: null,
        userId: this.userInfo.id,
        username: this.userInfo.username,
        receiverIds: roomMemberIds,
        messageBundleId: messageBundleId,
      };
      this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/awake`, JSON.stringify(msg));

      this.message = "";
      this.setAwakePrivateRoomStatus(false);
      this.setAwakePrivateRoomInfo(null);
      this.getChat();
      console.log(this.roomInfo);
    },
    // sendToAwakeOut(messageContent) {
    //   const msg = {
    //     type: 3,
    //     content: messageContent,
    //     roomId: this.roomStatus.roomId,
    //     roomType: 0,
    //     roomname: null,
    //     userId: this.userInfo.id,
    //     username: this.userInfo.username,
    //     receiverIds: this.roomMemberIds,
    //     messageBundleId: this.chatInfo.nextMessageBundleId,
    //   };
    //   this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/awake`, JSON.stringify(msg));

    //   this.message = "";
    // },
    sendToCreatePrivateRoom() {
      console.log("개인톡방생성 버튼 클릭");
      let members = [];
      let member = {
        userId: this.newPrivateRoomFriendInfo.id,
        username: this.newPrivateRoomFriendInfo.username,
        profile: JSON.stringify(this.newPrivateRoomFriendInfo.profile),
      };
      members.push(member);

      let userInfo = {
        userId: this.userInfo.id,
        username: this.userInfo.username,
        profile: JSON.stringify(this.userInfo.profile),
      };
      members.push(userInfo);
      const payload = {
        roomname: this.newPrivateRoomFriendInfo.username,
        img: this.newPrivateRoomFriendInfo.profile.profile,
        type: 0,
        members: members,
      };
      console.log(payload);
      this.$store.dispatch("socket/setTriggerMessage", this.message, { root: true });
      this.$store.dispatch("socket/createPrivateChat", payload, { root: true });
    },
    // 처음 방이 생성된 경우 자동으로 초대메세지를 전송합니다.
    sendInviteMessage() {
      if (this.stompChatRoomClient && this.stompChatRoomClient.connected) {
        let invitedFriendsNames = [];
        let invitedFriendsIds = [];
        let invitedFriends = this.newRoomInfo.members;
        invitedFriends.forEach((e) => {
          if (e.userId != this.userInfo.id) {
            invitedFriendsNames.push(e.username + "님");
          }
          invitedFriendsIds.push(e.userId);
        });
        this.roomInfo = this.newRoomInfo;
        const message = `${this.userInfo.username}님이 ${invitedFriendsNames.join(",")}을 초대했습니다.`;
        const msg = {
          type: 1,
          content: message,
          roomId: this.newRoomInfo.id,
          roomname: this.newRoomInfo.roomname,
          userId: this.userInfo.id,
          username: this.userInfo.username,
          receiverIds: invitedFriendsIds,
          messageBundleId: this.chatInfo.nextMessageBundleId,
        };
        this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/send`, JSON.stringify(msg));
      }
      this.setCreateChatRoomStatus(false);
    },
    sendTriggerMessage() {
      if (this.stompChatRoomClient && this.stompChatRoomClient.connected && this.triggerMessage) {
        console.log("=====트리거메세지 전송=====");
        let membersIds = [];
        let members = this.newRoomInfo.members;
        members.forEach((e) => {
          membersIds.push(e.userId);
        });
        this.roomInfo = this.newRoomInfo;
        const msg = {
          type: 0,
          content: this.triggerMessage,
          roomId: this.roomStatus.roomId,
          roomType: this.roomInfo.type,
          roomname: this.roomInfo.roomname,
          userId: this.userInfo.id,
          username: this.userInfo.username,
          receiverIds: membersIds,
          messageBundleId: this.chatInfo.nextMessageBundleId,
        };
        this.stompChatRoomClient.send(`/simple/chatroom/${this.roomStatus.roomId}/message/send`, JSON.stringify(msg));
        this.setNewPrivateRoomRefresh(true);
      }
      this.message = "";
      this.$store.dispatch("socket/clearNewPrivateRoom");
    },
    newPrivateRoom() {
      console.log("====개인톡방 오픈합니다====");
      console.log(this.newPrivateRoomFriendInfo);
      const roomInfo = {
        roomname: this.newPrivateRoomFriendInfo.username,
      };
      this.roomInfo = roomInfo;
    },

    openProfileModal(userProfileInfo) {
      this.$store.dispatch("modal/openProfileModal", { status: "open", userProfileInfo: userProfileInfo }, { root: true });
    },
    openSidebar() {
      this.$store.dispatch("modal/setSidebar", true, { root: true });

      const sidebar = document.querySelector(".sidebar-container");
      const sidebarBack = document.querySelector(".sidebar-background");
      sidebarBack.style.display = "block";
      sidebar.style.right = "0px";
    },
    exitChat() {
      if (!this.newPrivateRoomStatus) {
        const headers = { action: "leave" };
        this.stompChatRoomClient.disconnect(() => {}, headers);
      }
      this.$store.dispatch("chat/changePage", { mainPage: this.roomStatus.mainPage, chat: "chat", roomId: false }, { root: true });
    },

    //스크롤 최상단 위치하면 페이징 실행
    handleScroll(e) {
      const { scrollTop } = e.target;
      // 스크롤위치가 최상단이고 처음 입장한 경우에는 페이징 요청을 보내지않는다.
      // 해당 경우가 아닌 경우에만 요청 보내도록 설정
      if (scrollTop == 0 && this.moreMessages != 0) {
        this.getMessageHistory(e);
        this.$nextTick(() => {
          this.loadingIsActive = false;
        });
      }
    },
    getMessageHistory() {
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
          // 메세지목록에 추가 + 스크롤 기존 위치 지정
          // 최상단 도착시 추가안되도록 막음
          if (res.data.data.length && res.data.data[0].id != this.chatMessages[0].id) {
            this.chatMessages.unshift(...res.data.data);
            this.$nextTick(() => {
              let chatMessages = this.$refs.scrollRef;
              const nowScrollTo = chatMessages.scrollHeight - this.previousScrollHeight;
              chatMessages.scrollTo({ top: nowScrollTo });
              this.previousScrollHeight = chatMessages.scrollHeight;
              this.bottomScrollTop = chatMessages.scrollTop;
            });
          }
        });
    },
    scrollBottom() {
      let chatMessages = this.$refs.scrollRef;
      // 메세지가 왔을때 현재 스크롤 위치와 최하단 스크롤 위치가
      if (chatMessages && chatMessages.scrollTop - this.bottomScrollTop < 1000) {
        this.$nextTick(() => {
          let chatMessages = this.$refs.scrollRef;
          chatMessages.scrollTo({ top: this.previousScrollHeight, behavior: "smooth" });
          this.previousScrollHeight = chatMessages.scrollHeight;
        });
      }
    },
    getMessageType(chatFile) {
      if (chatFile.type.includes("image")) return 4;
      if (chatFile.type.includes("video")) return 5;
      else return 6;
    },
    // 파일을 업로드 할 때 마다 실행됩나다
    handleFileChange(e) {
      console.log("[Chat] Uploading files........", e.target.files[0]);
      let payload = { chatFile: e.target.files[0], chatFileThumb: this.makeTumbnail(e.target.files[0]), roomId: this.roomId };
      document.getElementById("file-input").value = ""; // input 초기화
      this.isLoading = true;
      console.log("로딩 중 : ", this.isLoading);
      // 파일 타입 구하기
      console.log("file type", payload.chatFile.type);
      this.$store
        .dispatch("chat/updateFile", payload)
        .then(({ data }) => {
          let fileUrl = data.data;
          console.log("[Chat] Uploaded file", fileUrl);
          // 파일 메시지를 보내는 함수입니다.
          this.send(this.getMessageType(payload.chatFile), fileUrl);
          this.isLoading = false;
          console.log("로딩 중 : ", this.isLoading);
        })
        .catch((e) => {
          this.isLoading = false;
          console.error(e);
        });
    },
    async makeTumbnail(img) {
      let convertFile = await this.convertFile(img);
      console.log(convertFile);
      let dataURI;
      let thumbnail;
      convertFile.onload = await function () {
        let canvas = document.createElement("canvas");
        let canvasContext = canvas.getContext("2d");

        canvas.width = 100;
        canvas.height = 100;

        canvasContext.drawImage(this, 0, 0, 100, 100);

        dataURI = canvas.toDataURL("image/jpeg");
        thumbnail = dataURI;
        return thumbnail;
      };
    },
    convertFile(img) {
      const reader = new FileReader();
      reader.readAsDataURL(img);
      reader.onload = () => {
        let tempImage = new Image();
        tempImage.src = reader.result;
        console.log("=======썸네일========");
        return tempImage;
      };
    },
  },
  filters: {
    videoThumbNail: function (value) {
      return value + "#t=0.5";
    },
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
.chat-room-title {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  width: 300px;

  padding-top: 0px;
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
  font-weight: bold;
}
.unread-number-me {
  color: #749f58;
  font-size: 10px;
  position: absolute;
  bottom: 11px;
  right: 1px;
  width: 12px;
  height: 15px;
  font-weight: bold;
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
.invite-message {
  background-color: #bad1ac;
  padding: 5px 0;
  text-align: center;
}

.image-upload {
  width: 50px;
  height: 50px;
  margin-right: 3px;
  text-align: center;
}
.image-upload > input {
  display: none;
}

.image-upload div :hover {
  background-color: #d4d4d3 !important;
  border-radius: 15%;
  cursor: pointer;
}

.image-upload label {
  margin: 0px;
  padding: 0px;
}

.image-upload .iconify {
  padding: 2px;
  margin-top: 30%;
  width: 30px;
  height: 30px;
}
.img-message {
  max-width: 60%;
  border-radius: 5%;
  cursor: pointer;
}
.video-message {
  max-width: 90%;
  border-radius: 5%;
}
.my-file-message {
  text-align: end;
}

.my-file-message-info {
  position: relative;
  width: 55px;
  display: inline-block;
  right: 10px;
}
.others-file-message-info {
  position: relative;
  width: 55px;
  display: inline-block;
  left: 5px;
}

.loading {
  position: absolute;
  bottom: 10px;
  /* left: 10px; */
  right: 50px;
  text-align: right;
  width: 100%;
}

.loading div {
  font-family: IBMPlexSansKR !important;
  font-weight: bold;
  display: inline-block;
}
.loading img {
  position: absolute;
  /* right: 10px; */
  width: 50px;
  bottom: -10px;
}
</style>
