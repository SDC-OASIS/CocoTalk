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
    chats: [],
    stompChatListClient: null,
    stompChatListConnected: false,
    stompChatRoomClient: null,
    stompChatRoomConnected: false,
    createChatRoomStatus: false,
    newPrivateRoomStatus: false,
    newPrivateRoomFriendInfo: {},
    newPrivateRoomRefresh: false,
    awakePrivateRoomStatus: false,
    awakePrivateRoomInfo: {},
    inviteRoomInfo: {},
    triggerMessage: null,
  },
  mutations: {
    SET_STOMP_CHAT_LIST_CLIENT(state, stompChatListClient) {
      state.stompChatListClient = stompChatListClient;
    },
    SET_STOMP_CHAT_ROOM_CLIENT(state, stompChatRoomClient) {
      state.stompChatRoomClient = stompChatRoomClient;
    },
    SET_STOMP_CHAT_LIST_CONNECTED(state, payload) {
      state.stompChatListConnected = payload;
    },
    SET_STOMP_CHAT_ROOM_CONNECTED(state, stompChatRoomConnected) {
      state.stompChatRoomConnected = stompChatRoomConnected;
    },
    SET_STOMP_CHAT_LIST_DISCONNECT(state) {
      state.stompChatListConnected = false;
    },
    SET_CREATE_CHAT_ROOM_STATUS(state, payload) {
      state.createChatRoomStatus = payload;
    },
    SET_NEW_PRIVATE_ROOM_STATUS(state, payload) {
      state.newPrivateRoomStatus = payload;
    },
    SET_NEW_PRIVATE_ROOM_FRIEND_INFO(state, payload) {
      state.newPrivateRoomFriendInfo = payload;
    },
    SET_INVITE_ROOM_INFO(state, payload) {
      state.inviteRoomInfo = payload;
    },
    SET_CHAT_LIST(state, payload) {
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
    SET_NEW_PRIVATE_ROOM_REFRESH(state, payload) {
      state.newPrivateRoomRefresh = payload;
    },
    SET_AWAKE_PRIVATE_ROOM_STATUS(state, payload) {
      state.awakePrivateRoomStatus = payload;
    },
    SET_AWAKE_PRIVATE_ROOM_INFO(state, friend) {
      state.awakePrivateRoomInfo = friend;
    },
  },
  actions: {
    // ????????? ?????? ?????? ????????? ????????? ??????????????? ??????
    checkConnect() {
      axios.get("auth/device").then((res) => {
        if (!res.data.result.isValid) {
          store.dispatch("userStore/logout");
          const payload = {
            status: "open",
            text: "?????? ???????????? ????????????????????????.",
          };
          store.dispatch("modal/openAlert", payload, { root: true });
        }
      });
    },
    // ????????? ?????? ????????? ????????????
    getChatList(context) {
      axios.get("chat/rooms/list").then((res) => {
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
          context.commit("SET_CHAT_LIST", chatList);
        }
      });
    },
    getChatUrl() {
      return new Promise((resolve, reject) => {
        {
          axios
            .get("presence/stomp/connect")
            .then((res) => {
              resolve(res.data.data);
            })
            .catch((err) => {
              reject(err);
            });
        }
      });
    },
    // ????????????????????? ???????????? ???????????? ??????????????? ????????? ??????
    async chatListConnect(context) {
      // const serverURL = "http://138.2.93.111:8080/stomp";
      const serverURL = await store.dispatch("socket/getChatUrl");
      let socket = new SockJS(serverURL);
      await context.commit("SET_STOMP_CHAT_LIST_CLIENT", Stomp.over(socket));
      await context.state.stompChatListClient.connect(
        { view: "chatList", userId: store.getters["userStore/userInfo"].id },
        () => {
          this.connected = true;
          context.commit("SET_STOMP_CHAT_LIST_CONNECTED", true);
          //[???????????? ????????? ????????? subscribe]
          context.state.stompChatListClient.subscribe(`/topic/${store.getters["userStore/userInfo"].id}/message`, (res) => {
            const data = JSON.parse(res.body);
            let lastMessage = data.message;
            let bundleInfo = data.bundleInfo;
            const idx = context.state.chats.findIndex(function (item) {
              return item.room.id == lastMessage.roomId;
            });
            // 1.????????? ????????? ??????
            if (context.state.chats[idx].room.type == 0 && lastMessage.type == 2) return;
            context.commit("UPDATE_CHAT_LIST", { idx, lastMessage });
            // ?????? ????????? ?????? ????????? ?????????????????? ?????????????????? ???????????? ????????? ??????????????? ?????????
            if (context.state.chats[idx].room.id != store.getters["chat/roomStatus"].roomId && lastMessage.type != 1 && lastMessage.type != 2) {
              context.commit("ADD_UNREAD_MESSAGES", idx);
            }
            // ????????? ?????? ???????????? ???????????? ??????
            if (idx) {
              const updateData = context.state.chats[idx];
              context.commit("DELETE_UADATED_CHATROOM", idx);
              context.commit("ADD_UADATED_CHATROOM", updateData);
            }

            // 2.???????????? ?????? ???????????? ?????? ??? ??????
            const newBundleCount = bundleInfo.currentMessageBundleCount;
            context.commit("UPADATE_BUNDLE_COUNT", { idx, newBundleCount });

            // 3.????????? ????????? ?????? ?????? ???????????? ?????? (???????????? 1???1 ??????) == ????????? ==
          });

          //[???????????? ??????????????? ?????? subscribe]
          context.state.stompChatListClient.subscribe(`/topic/${store.getters["userStore/userInfo"].id}/room`, (res) => {
            const newRoomInfo = JSON.parse(res.body);
            const idx = context.state.chats.findIndex(function (item) {
              return item.room.id == newRoomInfo.id;
            });
            context.commit("UPDATE_ROOM_INFO", { idx, newRoomInfo });
          });

          //[???????????? ?????? ????????? ??????????????? ?????? subscribe]
          context.state.stompChatListClient.subscribe(`/topic/${store.getters["userStore/userInfo"].id}/room/new`, (res) => {
            let newRoom = JSON.parse(res.body);
            newRoom.messageBundleIds = newRoom.messageBundleIds.slice(1, -1).split(", ");
            newRoom.members.forEach((e) => {
              if (e.profile) {
                e.profile = JSON.parse(e.profile);
              }
            });
            let chatRoom = {
              recentChatMessage: {},
              recentMessageBundleCount: 1, //count ??? ???????????????
              room: newRoom,
              unreadNumber: 0,
            };
            context.commit("ADD_NEW_CHAT_ROOM", chatRoom);
            if (context.state.createChatRoomStatus || context.state.newPrivateRoomStatus) {
              // ??????????????? ??????????????? ??????
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
          store.dispatch("socket/checkConnectSub");
        },
        () => {
          this.connected = false;
          context.commit("SET_STOMP_CHAT_LIST_CONNECTED", false);
        },
      );
    },
    // [?????? ???????????? ????????? ?????? ?????? ???????????? ?????? subscribe]
    checkConnectSub(context) {
      context.state.stompChatListClient.subscribe(`/topic/${store.getters["userStore/userInfo"].id}/crash/web`, (res) => {
        if (res.body != store.getters["userStore/userInfo"].fcmToken && store.getters["userStore/accessToken"]) {
          store.dispatch("userStore/logout");
          const payload = {
            status: "open",
            text: "?????? ???????????? ????????????????????????.",
          };
          store.dispatch("modal/openAlert", payload, { root: true });
        }
      });
    },
    goChat(context, chat) {
      let payload = {
        roomId: chat.room.id,
        nextMessageBundleId: chat.room.messageBundleIds[chat.room.messageBundleIds.length - 1],
        recentMessageBundleCount: chat.recentMessageBundleCount,
      };
      store.dispatch("chat/goChat", payload, { root: true });
      const idx = context.state.chats.findIndex(function (item) {
        return item.room.id == chat.room.id;
      });
      // ?????? ?????? ???????????? 0?????? ?????????
      context.commit("ENTER_CHAT_ROOM", idx);
    },

    createChat(context, data) {
      context.state.stompChatListClient.send("/simple/chatroom/new", JSON.stringify(data));
      store.dispatch("modal/closeRoomNameEditModal");
      context.commit("SET_CREATE_CHAT_ROOM_STATUS", true);
    },
    startPrivateChat(context, friend) {
      store.dispatch("modal/closeProfileModal");
      if (!friend.id) {
        friend.id = friend.userId;
      }
      // ?????? ????????? ??????????????? ????????? ??????
      axios.get(`chat/rooms/private/${friend.id}`).then((res) => {
        // ?????? ?????? ??????
        if (res.data.data.id) {
          context.commit("SET_NEW_PRIVATE_ROOM_STATUS", false);
          res.data.data.members.forEach((e) => {
            if (e.userId == store.getters["userStore/userInfo"].id && !e.joining) {
              context.commit("SET_AWAKE_PRIVATE_ROOM_STATUS", true);
              res.data.data.messageBundleIds = res.data.data.messageBundleIds.slice(1, -1).split(", ");
              context.commit("SET_AWAKE_PRIVATE_ROOM_INFO", res.data.data);
            }
          });
          router.push({ name: store.getters["chat/roomStatus"].mainPage + "Chat", params: { chat: "chat", roomId: res.data.data.id } }).catch(() => {});
        }
        // ?????? ?????? ??????
        else {
          context.commit("SET_NEW_PRIVATE_ROOM_FRIEND_INFO", friend);
          context.commit("SET_NEW_PRIVATE_ROOM_STATUS", true);
          router.push({ name: store.getters["chat/roomStatus"].mainPage + "Chat", params: { chat: "chat", roomId: "private" } }).catch(() => {});
        }
      });
    },
    createPrivateChat(context, data) {
      context.state.stompChatListClient.send("/simple/chatroom/new", JSON.stringify(data));
      context.commit("SET_NEW_PRIVATE_ROOM_STATUS", true);
    },
    setTriggerMessage(context, msg) {
      context.commit("SET_TRIGGER_MASSAGE", msg);
    },
    clearNewPrivateRoom(context) {
      context.commit("CLEAR_NEW_PRIVATE_ROOM");
    },
    // ????????? ???????????? ????????????
    inviteFriend(context, friends) {
      if (!friends.length) {
        return;
      }
      // ???????????? ????????? ?????? ?????????
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
      // ????????? ?????? ????????? ?????? ?????????
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
      let members = [...invitees, ...previousMembers]; // ?????? ???????????? ???????????? ?????? ?????? ?????? ??????

      if (context.state.stompChatRoomClient && context.state.stompChatRoomClient.connected) {
        let invitedFriendsNames = [];
        invitees.forEach((e) => {
          if (e.userId != store.getters["userStore/userInfo"].id) {
            invitedFriendsNames.push(e.username + "???");
          }
        });
        let roomname = [];
        let receiverIds = [];
        members.forEach((e) => {
          roomname.push(e.username);
          receiverIds.push(e.userId);
        });
        const message = `${store.getters["userStore/userInfo"].username}?????? ${inviteesName.sort().join(",")}??? ??????????????????.`;
        const msg = {
          roomId: store.getters["chat/roomStatus"].roomId,
          roomType: 1,
          roomname: roomname.sort().join(","),
          userId: store.getters["userStore/userInfo"].id,
          username: store.getters["userStore/userInfo"].username,
          receiverIds: previousMemberIds,
          type: 1,
          content: message,
          invitees: invitees,
          messageBundleId: store.getters["chat/chatInfo"].nextMessageBundleId,
        };
        store.dispatch("modal/setSidebar", false, { root: true });
        context.state.stompChatRoomClient.send(`/simple/chatroom/${context.state.inviteRoomInfo.id}/message/invite`, JSON.stringify(msg));
      }
    },
    // ????????? ?????????
    exitChat(context, roomInfo) {
      if (context.state.stompChatRoomClient && context.state.stompChatRoomClient.connected) {
        let membersIds = [];
        roomInfo.members.forEach((e) => {
          if (e.userId != store.getters["userStore/userInfo"].id) {
            membersIds.push(e.userId);
          }
        });
        this.roomInfo = this.newRoomInfo;
        const message = `${store.getters["userStore/userInfo"].username}?????? ???????????????.`;
        const msg = {
          type: 2,
          content: message,
          roomId: roomInfo.id,
          roomname: roomInfo.roomname,
          userId: store.getters["userStore/userInfo"].id,
          username: store.getters["userStore/userInfo"].username,
          receiverIds: membersIds,
          messageBundleId: store.getters["chat/chatInfo"].nextMessageBundleId,
        };
        context.state.stompChatRoomClient.send(`/simple/chatroom/${roomInfo.id}/message/leave`, JSON.stringify(msg));
        const idx = context.state.chats.findIndex(function (item) {
          return item.room.id == roomInfo.id;
        });
        context.commit("DELETE_UADATED_CHATROOM", idx);
        router.push({ name: store.getters["chat/roomStatus"].mainPage });
      }
    },
  },
  modules: {},
};

export default socket;
