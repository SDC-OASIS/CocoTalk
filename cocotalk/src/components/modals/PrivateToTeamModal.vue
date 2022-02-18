<template>
  <div class="modal row" @click.self="closePrivateToTeamModal">
    <div class="modal-container">
      <div @click="closePrivateToTeamModal">
        <span class="iconify exit" data-icon="bx:bx-x"></span>
      </div>
      <div class="modal-inner-container">
        <div class="roomname-modal-header">
          <div style="font-size: 20px">그룹채팅방 정보 설정</div>
        </div>
        <div class="roomname-modal-info row" style="justify-content: center">
          <div style="dispaly: inline-block; text-align: center">
            <div v-if="privateToTeamModal.selectedFriends.length == 1">
              <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 70px" />
            </div>
            <div v-if="privateToTeamModal.selectedFriends.length == 2" style="width: 110px; height: 120px">
              <div style="position: absolute">
                <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 70px" />
                <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 70px" class="two-friends-second-img" :radius="1" />
              </div>
            </div>
            <div v-if="privateToTeamModal.selectedFriends.length == 3" style="width: 110px; height: 90px; padding-top: 50px">
              <div style="position: absolute">
                <profile-img :imgUrl="'https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg'" width=" 70px" />
                <profile-img :imgUrl="'https://ifh.cc/g/qKgD7C.png'" width=" 70px" class="three-friends-second-img" :radius="1" />
                <profile-img :imgUrl="'https://ifh.cc/g/CgiChn.jpg'" width=" 70px" class="three-friends-third-img" :radius="2" />
              </div>
            </div>
            <div v-if="privateToTeamModal.selectedFriends.length >= 4" style="width: 110px; height: 90px; padding-top: 50px">
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
          <div @click="closePrivateToTeamModal">
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
  name: "PrivateToTeamModal",
  data() {
    return {
      roomName: "",
      payload: {},
    };
  },
  computed: {
    ...mapState("modal", ["privateToTeamModal"]),
    ...mapState("userStore", ["userInfo"]),
    ...mapState("socket", ["inviteRoomInfo"]),

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
    closePrivateToTeamModal() {
      this.$store.dispatch("modal/closePrivateToTeamModal");
    },
    changeName(e) {
      this.roomName = "";
      this.roomName = e.target.value;
    },
    setCreateChatRoom() {
      console.log("갠톡에서 단톡으로 모달실행");
      console.log("룸멤버");
      console.log(this.inviteRoomInfo);
      console.log("신규멤버");
      console.log(this.privateToTeamModal.selectedFriends);

      // 기존멤버 데이터 형식 맞추기
      let previousMembers = [];
      let previousMemberIds = [];
      let previousMemberNames = [];
      this.inviteRoomInfo.members.forEach((e) => {
        let previousMember = {
          userId: e.userId,
          username: e.username,
          profile: JSON.stringify(e.profile),
        };
        previousMembers.push(previousMember);
        previousMemberIds.push(e.userId);
        previousMemberNames.push(e.username);
      });
      // 초대된 친구 데이터 형식 맞추기
      let invitees = [];
      let inviteeIds = [];
      let inviteesName = [];
      this.privateToTeamModal.selectedFriends.forEach((e) => {
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

      let roomname = [...previousMemberNames, ...inviteesName];
      this.roomName = roomname.sort().join(", ");
      const payload = {
        roomname: this.roomName,
        img: null,
        type: 1,
        members: [...previousMembers, ...invitees],
      };
      this.payload = payload;
    },
    createChatRoom() {
      this.payload.roomname = this.roomName;
      this.$store.dispatch("modal/setSidebar", false, { root: true });
      this.$store.dispatch("modal/closePrivateToTeamModal");
      this.$store.dispatch("socket/createChat", this.payload, { root: true });
    },
  },
  created() {
    this.setCreateChatRoom();
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
