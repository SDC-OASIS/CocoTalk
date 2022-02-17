<template>
  <div class="modal row" @click.self="closeRoomNameEditModal">
    <div class="modal-container">
      <div @click="closeRoomNameEditModal">
        <span class="iconify exit" data-icon="bx:bx-x"></span>
      </div>
      <div class="modal-inner-container">
        <div class="roomname-modal-header">
          <div style="font-size: 20px">그룹채팅방 정보 설정</div>
        </div>
        <div class="roomname-modal-info row" style="justify-content: center">
          <div style="dispaly: inline-block; text-align: center">
            <div v-if="roomNameEditModal.selectedFriends.length == 1">
              <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 70px" />
            </div>
            <div v-if="roomNameEditModal.selectedFriends.length == 2" style="width: 110px; height: 120px">
              <div style="position: absolute">
                <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 70px" />
                <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 70px" class="two-friends-second-img" :radius="1" />
              </div>
            </div>
            <div v-if="roomNameEditModal.selectedFriends.length == 3" style="width: 110px; height: 90px; padding-top: 50px">
              <div style="position: absolute">
                <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 70px" />
                <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 70px" class="three-friends-second-img" :radius="1" />
                <profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 70px" class="three-friends-third-img" :radius="2" />
              </div>
            </div>
            <div v-if="roomNameEditModal.selectedFriends.length >= 4" style="width: 110px; height: 90px; padding-top: 50px">
              <div style="position: absolute">
                <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" class="four-friends-first-img" width=" 60px" />
                <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 60px" class="four-friends-second-img" :radius="1" />
                <profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 60px" class="four-friends-third-img" :radius="2" />
                <profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 60px" class="four-friends-forth-img" :radius="2" />
              </div>
            </div>
          </div>
        </div>
        <div>
          <div class="roomname-modal-input row">
            <input :value="roomName" type="text" maxlength="50" @input="changeName" />
            <span>
              {{ friendIdCnt + "/50" }}
            </span>
          </div>
        </div>
        <hr />
        <div style="font-size: 15px; color: #535353">설정한 그룹채팅방의 사진과 이름은 다른 모든 대화상대에게도 동일하게 보입니다.</div>
        <div class="row" style="justify-content: right; padding-top: 10px">
          <div @click="createChatRoom">
            <Button text="확인" width="50px" height="30px" style="margin-right: 20px" />
          </div>
          <div @click="closeRoomNameEditModal">
            <Button text="취소" width="50px" height="30px" backgroundColor="#ffffff" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import ProfileImg from "../common/ProfileImg.vue";
import Button from "../common/Button.vue";
export default {
  name: "RoomNameEditModal",
  data() {
    return {
      roomName: "",
    };
  },
  computed: {
    ...mapState("modal", ["roomNameEditModal"]),
    ...mapState("userStore", ["userInfo"]),
    friendIdCnt() {
      if (this.roomName.length) {
        return this.roomName.length;
      } else {
        return 0;
      }
    },
  },

  components: {
    ProfileImg,
    Button,
  },
  methods: {
    closeRoomNameEditModal() {
      this.$store.dispatch("modal/closeRoomNameEditModal");
    },
    changeName(e) {
      this.roomName = "";
      this.roomName = e.target.value;
    },
    setroomName() {
      console.log("채팅방 이름 설정");
      let selectedNames = "";
      this.roomNameEditModal.selectedFriends.forEach((e) => {
        selectedNames += e.username + ", ";
      });
      this.roomName = selectedNames;
    },
    createChatRoom() {
      "채팅방생성 버튼 클릭";
      let type = 0;
      if (this.roomNameEditModal.selectedFriends.length > 1) {
        type = 1;
      }
      let members = [];
      this.roomNameEditModal.selectedFriends.forEach((e) => {
        let member = {
          userId: e.id,
          username: e.username,
          profile: JSON.stringify(e.profile),
        };
        members.push(member);
      });
      let userInfo = {
        userId: this.userInfo.id,
        username: this.userInfo.username,
        profile: JSON.stringify(this.userInfo.profile),
      };
      members.push(userInfo);
      const payload = {
        roomname: this.roomName,
        img: null,
        type: type,
        members: members,
      };
      console.log(payload);
      this.$store.dispatch("socket/createChat", payload, { root: true });
    },
  },
  created() {
    this.setroomName();
  },
};
</script>

<style scoped>
.modal-container {
  width: 400px;
  height: 400px;
  background-color: #ffffff;
  border-radius: 10px;
  position: relative;
}
.exit {
  position: absolute;
  top: 0;
  right: 0;
  margin: 13px;
  font-size: 30px;
  color: #42652b;
  cursor: pointer;
}
.modal-inner-container {
  padding: 25px 30px;
  text-align: left;
}
.roomname-modal-header > div {
  color: #42652b;
  font-weight: bold;
  margin: 10px 0;
}
.roomname-modal-input {
  text-align: center;
  border-radius: 20px;
  height: 35px;
  align-items: center;
  justify-content: center;
}
.roomname-modal-input > input {
  /* display: block; */
  border: none;
  border-radius: 20px;
  /* margin: 0 0 20px 0; */
  width: 90%;
  /* height: 35px; */
  font-size: 20px;
  color: #aaaaaa;
}
.roomname-modal-input > input::placeholder {
  font-size: 17px;
  color: #aaaaaa;
}
.roomname-modal-input > input:focus {
  outline: none;
  /* outline: 2px solid #fce41e; */
}
.roomname-modal-input > span {
  height: 20px;
  line-height: 20px;
  padding-right: 20px;
  color: #9eac95;
  font-weight: bold;
  /* outline: 2px solid #fce41e; */
}
.roomname-modal-info {
  padding: 25px 0 5px 0;
  margin-left: -20px;
}

.two-friends-second-img {
  position: relative;
  top: -30px;
  left: -30px;
}

.three-friends-second-img {
  position: relative;
  top: -75px;
  left: -10px;
}
.three-friends-third-img {
  position: relative;
  top: -135px;
  left: -45px;
}
.four-friends-first-img {
  position: relative;
  top: 5px;
  left: 10px;
  z-index: 3;
}
.four-friends-second-img {
  position: relative;
  top: -60px;
  left: 10px;
  z-index: 3;
}
.four-friends-third-img {
  position: relative;
  top: -125px;
  left: -55px;
}
.four-friends-forth-img {
  position: relative;
  top: -125px;
  left: 10px;
}
</style>
