<template>
  <div class="modal row" @click.self="closeSidebarFIlesModal">
    <div class="modal-container">
      <div @click="closeSidebarFIlesModal">
        <span class="iconify exit" data-icon="bx:bx-x"></span>
      </div>
      <div class="roomname-modal-header">
        <div style="font-size: 20px">사진, 동영상</div>
      </div>
      <div class="modal-inner-container">
        <div class="media-img-container" v-for="(message, idx) in files" :key="idx" @click="openClick(message.content)">
          <img :src="message.content" alt="" class="box" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "SidebarFilesModal",
  props: {
    files: [],
  },
  methods: {
    closeSidebarFIlesModal() {
      this.$store.dispatch("modal/closeSidebarFilesModal");
    },
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
      OpenWindow.document.write(`<style>body{margin:0px;}</style><img src='${url}' width='$${win_width}'>`);
    },
  },
};
</script>

<style scoped>
.modal-container {
  width: 700px;
  height: 700px;
  background-color: #ffffff;
  border-radius: 10px;
  position: relative;
}

.exit {
  position: absolute;
  top: 0;
  right: 0;
  margin: 30px;
  font-size: 30px;
  color: #42652b;
  cursor: pointer;
}
.modal-inner-container {
  padding: 25px 25px;
  text-align: left;
  width: 625px;
  height: 500px;
  overflow: auto;
}
.modal-inner-container::-webkit-scrollbar {
  position: absolute;
  background-color: #d8eec0;
  width: 23px;
}
.modal-inner-container::-webkit-scrollbar-track {
  background-color: #ffffff;
  width: 10px;
}
.modal-inner-container::-webkit-scrollbar-thumb {
  background-color: #b8c8ae;
  border-radius: 10px;
  width: 10px;
  background-clip: padding-box;
  border: 8px solid transparent;
}
.roomname-modal-header > div {
  color: #42652b;
  font-weight: bold;
  margin: 30px 0;
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
.media-img-container {
  width: 150px;
  margin: 0;
  display: inline-block;
}
.media-img-container > img {
  width: 140px;
  height: 140px;
  padding: 10px 10px 5px 0;
  margin: 0;
  cursor: pointer;
  display: inline-block;
}
.polaroid {
  background: #000; /*Change this to a background image or remove*/
  border: solid #fff;
  border-width: 6px 6px 20px 6px;
  box-shadow: 1px 1px 5px #333; /* Standard blur at 5px. Increase for more depth */
  -webkit-box-shadow: 1px 1px 5px #333;
  -moz-box-shadow: 1px 1px 5px #333;
  height: 200px; /*Set to height of your image or desired div*/
  width: 200px; /*Set to width of your image or desired div*/
}
</style>
