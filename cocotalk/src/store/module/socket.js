import createPersistedState from "vuex-persistedstate";
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import axios from "@/utils/axios";
import router from "../../router";
import store from "@/store";

const socket = {
  plugins: [createPersistedState()],
  namespaced: true,
  state: {
    stompChatListClient: null,
    stompChatListConnected: false,
    stompChatRoomClient: null,
    stompChatRoomConnected: false,
    createChatRoomStatus: false,
    newPrivateRoomStatus: false,
    newPrivateRoomFriendInfo: {},
    newPrivateRoomRefresh: false,
    inviteRoomInfo: {},
    triggerMessage: null,
    chats: [],
  },
  mutations: {
    setStompChatListClient(state, stompChatListClient) {
      state.stompChatListClient = stompChatListClient;
    },
    setStompChatRoomClient(state, stompChatRoomClient) {
      state.stompChatRoomClient = stompChatRoomClient;
    },
    setStompChatListConnected(state, payload) {
      state.stompChatListConnected = payload;
    },
    setStompChatRoomConnected(state, stompChatRoomConnected) {
      state.stompChatRoomConnected = stompChatRoomConnected;
    },
    setStompChatListDisconnect(state) {
      state.stompChatListConnected = false;
    },
    setCreateChatRoomStatus(state, payload) {
      state.createChatRoomStatus = payload;
    },
    setNewPrivateRoomStatus(state, payload) {
      state.newPrivateRoomStatus = payload;
    },
    setNewPrivateRoomFriendInfo(state, payload) {
      state.newPrivateRoomFriendInfo = payload;
    },
    setInviteRoomInfo(state, payload) {
      state.inviteRoomInfo = payload;
    },
    setChatList(state, payload) {
      state.chats = payload;
    },
    UPDATE_CHAT_LIST(state, { idx, lastMessage }) {
      state.chats[idx].recentChatMessage = lastMessage;
    },
    UPDATE_ROOM_INFO(state, { idx, newRoomInfo }) {
      state.chats[idx].room = newRoomInfo;
    },
    ADD_UNREAD_MESSAGES(state, idx) {
      state.chats[idx].unreadNumber++;
    },
    DELETE_UADATED_CHATROOM(state, idx) {
      state.chats.splice(idx, 1);
    },
    ADD_UADATED_CHATROOM(state, updateData) {
      state.chats.unshift(updateData);
    },
    UPADATE_BUNDLE_COUNT(state, { idx, newBundleCount }) {
      state.chats[idx].recentMessageBundleCount = newBundleCount;
    },
    ENTER_CHAT_ROOM(state, idx) {
      state.chats[idx].unreadNumber = 0;
    },
    ADD_NEW_CHAT_ROOM(state, chatRoom) {
      state.chats.unshift(chatRoom);
    },
    SET_TRIGGER_MASSAGE(state, msg) {
      state.triggerMessage = msg;
    },
    CLEAR_NEW_PRIVATE_ROOM(state) {
      state.triggerMessage = null;
      state.newPrivateRoomStatus = false;
    },
    setNewPrivateRoomRefresh(state, payload) {
      console.log("바뀌라고!!!");
      state.newPrivateRoomRefresh = payload;
    },
  },
  actions: {
    getChatList(context) {
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
            if (e.room.id == store.getters["chat/roomStatus"].roomId) {
              e.unreadNumber = 0;
            }
          });
          context.commit("setChatList", chatList);
        }
        console.log(chatList);
      });
    },
    async chatListConnect(context) {
      const serverURL = "http://138.2.93.111:8080/stomp";
      let socket = new SockJS(serverURL);
      await context.commit("setStompChatListClient", Stomp.over(socket));
      await context.state.stompChatListClient.connect(
        { view: "chatList", userId: store.getters["userStore/userInfo"].id },
        (frame) => {
          this.connected = true;
          context.commit("setStompChatListConnected", true);
          console.log("소켓 연결 성공", frame);
          //[채팅목록 마지막 메세지 subscribe]
          context.state.stompChatListClient.subscribe(`/topic/${store.getters["userStore/userInfo"].id}/message`, (res) => {
            console.log("구독으로 받은 메시지목록의 마지막 메세지 정보 입니다.");
            const data = JSON.parse(res.body);
            let lastMessage = data.message;
            let bundleInfo = data.bundleInfo;
            console.log(data);
            const idx = context.state.chats.findIndex(function (item) {
              return item.room.id == lastMessage.roomId;
            });
            // 1.마지막 메세지 갱신
            context.commit("UPDATE_CHAT_LIST", { idx, lastMessage });
            // 현재 입장한 방이 아니고 초대메세지가 아니라면 안읽은 메세지수에 더해줌
            if (context.state.chats[idx].room.id != store.getters["chat/roomStatus"].roomId && lastMessage.type != 1) {
              context.commit("ADD_UNREAD_MESSAGES", idx);
            }
            // 채팅방 목록 최상단에 있지않은 경우
            if (idx) {
              const updateData = context.state.chats[idx];
              console.log(updateData);
              context.commit("DELETE_UADATED_CHATROOM", idx);
              context.commit("ADD_UADATED_CHATROOM", updateData);
            }

            // 2.채팅방별 최신 메세지의 번들 수 갱신
            const newBundleCount = bundleInfo.currentMessageBundleCount;
            context.commit("UPADATE_BUNDLE_COUNT", { idx, newBundleCount });

            // 3.채팅방 목록에 없는 방의 메세지인 경우 (나가기한 1대1 채팅) == 구현중 ==
          });

          //[채팅목록 채팅방정보 채널 subscribe] == 구현중 ==
          context.state.stompChatListClient.subscribe(`/topic/${store.getters["userStore/userInfo"].id}/room`, (res) => {
            console.log("구독으로 받은 업데이트된 룸정보입니다.");
            console.log(res);
            const newRoomInfo = JSON.parse(res.body);
            const idx = context.state.chats.findIndex(function (item) {
              return item.room.id == newRoomInfo.id;
            });
            context.commit("UPDATE_ROOM_INFO", { idx, newRoomInfo });
          });

          //[채팅목록 새로 생성된 채팅방정보 채널 subscribe] == 구현중 ==
          context.state.stompChatListClient.subscribe(`/topic/${store.getters["userStore/userInfo"].id}/room/new`, (res) => {
            console.log("구독으로 받은 새로 생성된 룸정보입니다.");
            console.log(JSON.parse(res.body));
            let newRoom = JSON.parse(res.body);
            // let bundleInfo = JSON.parse(res.body).bundleInfoVo;
            console.log(newRoom);
            newRoom.messageBundleIds = newRoom.messageBundleIds.slice(1, -1).split(", ");
            newRoom.members.forEach((e) => {
              if (e.profile) {
                e.profile = JSON.parse(e.profile);
              }
            });
            console.log(newRoom);
            let chatRoom = {
              recentChatMessage: {},
              recentMessageBundleCount: 1, //count 값 갱신해주기
              room: newRoom,
              unreadNumber: 0,
            };
            console.log(context.state.chats);
            context.commit("ADD_NEW_CHAT_ROOM", chatRoom);
            console.log("===방생성중[crateChatRoomStatus:" + context.state.createChatRoomStatus + "]===");
            if (context.state.createChatRoomStatus || context.state.newPrivateRoomStatus) {
              // 새로생성된 채팅방으로 가기
              let newRoomInfo = {
                roomId: newRoom.id,
                nextMessageBundleId: newRoom.messageBundleIds[0],
                recentMessageBundleCount: 1,
                newRoom: newRoom,
              };
              store.dispatch("chat/updateMessageBundleCount", 1, { root: true });
              store.dispatch("chat/goNewChat", newRoomInfo, { root: true });
            }
          });
        },
        (error) => {
          console.log("소켓 연결 실패", error);
          this.connected = false;
          context.commit("setStompChatListConnected", false);
        },
      );
      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`);
    },
    goChat(context, chat) {
      // 현재 swagger로 채팅방생성중이기때문에 첫메세지가 없는 경우가 있어 nextMessageBundleId 업데이트.
      // 채팅방내부에서 채팅내역불러와서 갱신해주기 때문에 이후에는 필요없음
      let payload = {
        roomId: chat.room.id,
        nextMessageBundleId: chat.room.messageBundleIds[chat.room.messageBundleIds.length - 1],
        recentMessageBundleCount: chat.recentMessageBundleCount,
      };
      store.dispatch("chat/goChat", payload, { root: true });
      const idx = context.state.chats.findIndex(function (item) {
        return item.room.id == chat.room.id;
      });
      // 읽지 않은 메세지수 0으로 만들기
      context.commit("ENTER_CHAT_ROOM", idx);
    },

    createChat(context, data) {
      console.log("채팅방생성");
      context.state.stompChatListClient.send("/simple/chatroom/new", JSON.stringify(data));
      store.dispatch("modal/closeRoomNameEditModal");
      context.commit("setCreateChatRoomStatus", true);
    },
    startPrivateChat(context, friend) {
      console.log("개인톡방 체크");
      store.dispatch("modal/closeProfileModal");
      if (!friend.id) {
        friend.id = friend.userId;
      }
      axios.get(`chat/rooms/private/${friend.id}`).then((res) => {
        console.log("개인톡방 있나요?");
        if (res.data.data.id) {
          context.commit("setNewPrivateRoomStatus", false);
          console.log("방있어요");
          console.log(res.data.data.id);
          router.push({ name: store.getters["chat/roomStatus"].mainPage + "Chat", params: { chat: "chat", roomId: res.data.data.id } }).catch(() => {});
        } else {
          console.log("방없어요");
          context.commit("setNewPrivateRoomFriendInfo", friend);
          context.commit("setNewPrivateRoomStatus", true);
          router.push({ name: store.getters["chat/roomStatus"].mainPage + "Chat", params: { chat: "chat", roomId: "private" } }).catch(() => {});
        }
      });
    },
    createPrivateChat(context, data) {
      console.log("개인톡방생성");
      context.state.stompChatListClient.send("/simple/chatroom/new", JSON.stringify(data), (res) => {
        console.log("생성결과");
        console.log(res);
      });
      // store.dispatch("modal/closeRoomNameEditModal"); 아마 프로필모달닫기?
      context.commit("setNewPrivateRoomStatus", true);
    },
    setTriggerMessage(context, msg) {
      context.commit("SET_TRIGGER_MASSAGE", msg);
    },
    clearNewPrivateRoom(context) {
      context.commit("CLEAR_NEW_PRIVATE_ROOM");
    },
    // 채팅방 내부에서 친구초대
    inviteFriend(context, friends) {
      console.log("친구초대시작합니다.");
      console.log("방정보");
      console.log(context.state.inviteRoomInfo);
      console.log("friends");
      console.log(friends);
      // 기존멤버 데이터 형식 맞추기
      let previousMembers = [];
      let previousMemberIds = [];
      context.state.inviteRoomInfo.members.forEach((e) => {
        let previousMember = {
          userId: e.userId,
          username: e.username,
          profile: JSON.stringify(e.profile),
        };
        previousMembers.push(previousMember);
        previousMemberIds.push(e.userId);
      });
      // 초대된 친구 데이터 형식 맞추기
      let invitees = [];
      let inviteeIds = [];
      let inviteesName = [];
      friends.forEach((e) => {
        if (!previousMemberIds.includes(e.id)) {
          let invitee = {
            userId: e.id,
            username: e.username,
            profile: JSON.stringify(e.profile),
          };
          invitees.push(invitee);
          inviteeIds.push(invitee.userId);
          inviteesName.push(invitee.username);
        }
      });
      console.log(invitees);
      console.log(previousMembers);
      let members = [...invitees, ...previousMembers];
      // const setMembers = [...new Map(members.map((item) => [item.userId, item])).values()];
      console.log(members);
      console.log("=====필터: 이미 채팅방에 참여한 멤버 거르기 완료=====");

      if (context.state.stompChatRoomClient && context.state.stompChatRoomClient.connected) {
        console.log(context.state.inviteRoomInfo);
        let invitedFriendsNames = [];
        // let invitedFriendsIds = [];
        // let invitedFriends = friends;
        invitees.forEach((e) => {
          if (e.userId != store.getters["userStore/userInfo"].id) {
            invitedFriendsNames.push(e.username + "님");
          }
        });
        let roomname = [];
        let receiverIds = [];
        members.forEach((e) => {
          roomname.push(e.username);
          receiverIds.push(e.userId);
        });
        const message = `${store.getters["userStore/userInfo"].username}님이 ${inviteesName.sort().join(",")}을 초대했습니다.`;
        const msg = {
          roomId: context.state.inviteRoomInfo.id,
          roomType: 1,
          roomname: roomname.sort().join(","),
          userId: store.getters["userStore/userInfo"].id,
          username: store.getters["userStore/userInfo"].username,
          receiverIds: previousMemberIds,
          type: 1,
          content: message,
          invitees: invitees,
          messageBundleId: context.state.inviteRoomInfo.messageBundleIds[context.state.inviteRoomInfo.messageBundleIds.length - 1],
        };
        console.log(msg);
        context.state.stompChatRoomClient.send(`/simple/chatroom/${context.state.inviteRoomInfo.id}/message/invite`, JSON.stringify(msg));
      }
      this.message = "";
    },
  },
  modules: {},
};

export default socket;
