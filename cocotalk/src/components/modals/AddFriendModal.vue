<template>
  <div class="modal row" @click.self="closeAddFriendModal">
    <div class="modal-container">
      <div @click="closeAddFriendModal">
        <span class="iconify exit" data-icon="bx:bx-x"></span>
      </div>
      <div class="modal-inner-container">
        <div class="add-friend-modal-header">
          <div style="font-size: 20px">친구추가</div>
          <div>ID로 추가</div>
        </div>
        <hr />
        <div>
          <div class="add-friend-modal-input row">
            <input v-model.trim="friendId" type="text" placeholder="ID를 입력하세요." maxlength="20" @keypress.enter="getFriendInfo" />
            <span>
              {{ friendIdCnt + "/20" }}
            </span>
          </div>
          <div class="add-friend-modal-info row" style="justify-content: center">
            <div v-if="friendInfo" style="dispaly: inline-block; text-align: center">
              <div>
                <profile-img :imgUrl="friendInfo.friend.profile.profile" width=" 70px" />
              </div>
              <div style="font-weight: bold">{{ this.friendInfo.friend.username }}</div>
              <div>{{ this.friendInfo.friend.profile.message }}</div>
            </div>
          </div>
        </div>
        <div v-if="friendInfo" class="row" style="justify-content: right">
          <Button v-if="friendInfo.type == 0" text="1:1 채팅" width="80px" height="30px" />
          <Button v-else-if="friendInfo.type == 1 || friendInfo.type == 2" text="친구추가" width="80px" height="30px" @click.native="addFriend" />
          <Button v-else-if="friendInfo.type == 3" text="나와의 채팅" width="100px" height="30px" />
        </div>
        <!-- <div v-else class="row" style="justify-content: right" @click="getFriendInfo">
					<Button text="검색" width="80px" height="30px" />
				</div> -->
      </div>
    </div>
  </div>
</template>

<script>
import ProfileImg from "../common/ProfileImg.vue";
import Button from "../common/Button.vue";
import axios from "@/utils/axios";
export default {
  name: "AddFriendModal",
  data() {
    return {
      friendId: "",
      friendInfo: null,
    };
  },
  computed: {
    friendIdCnt() {
      if (this.friendId.length) {
        return this.friendId.length;
      } else {
        return 0;
      }
    },
  },
  watch: {
    friendId() {
      if (this.friendId == "") {
        this.friendInfo = null;
      }
    },
  },
  components: {
    ProfileImg,
    Button,
  },
  methods: {
    closeAddFriendModal() {
      this.$store.dispatch("modal/closeAddFriendModal");
    },
    getFriendInfo() {
      if (this.friendId) {
        axios.get(`user/friends/cid/${this.friendId}`).then((res) => {
          console.log("친구정보 가져오기");
          let friendInfo = res.data.data;
          if (friendInfo.friend.id) {
            friendInfo.friend.profile = JSON.parse(friendInfo.friend.profile);
            this.friendInfo = friendInfo;
            console.log(this.friendInfo);
          }
        });
      }
    },
    addFriend() {
      const friendId = {
        toUid: this.friendInfo.friend.id,
      };
      this.$store.dispatch("friend/addFriend", friendId, { root: true });
    },
  },
};
</script>

<style scoped>
.modal-container {
  width: 400px;
  height: 350px;
  background-color: #ffffff;
  border-radius: 10px;
  position: relative;
  cursor: pointer;
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
.add-friend-modal-header > div {
  color: #42652b;
  font-weight: bold;
  margin: 10px 0;
}
.add-friend-modal-input {
  text-align: center;
  border-radius: 20px;
  height: 35px;
  background: #d8eec0;
  align-items: center;
  justify-content: center;
}
.add-friend-modal-input > input {
  /* display: block; */
  border: none;
  border-radius: 20px;
  /* margin: 0 0 20px 0; */
  padding: 0 8%;
  width: 90%;
  /* height: 35px; */
  background: #d8eec0;
  font-size: 20px;
}
.add-friend-modal-input > input::placeholder {
  font-size: 17px;
  color: #749f58;
}
.add-friend-modal-input > input:focus {
  outline: none;
  /* outline: 2px solid #fce41e; */
}
.add-friend-modal-input > span {
  height: 20px;
  line-height: 20px;
  padding-right: 20px;
  color: #9eac95;
  font-weight: bold;
  /* outline: 2px solid #fce41e; */
}
.add-friend-modal-info {
  padding: 25px 0 5px 0;
}
</style>
