<template>
  <div class="box">
    <!-- <img :src="imgUrl" :style="{ width: width }" @error="$event.target.src = 'https://ifh.cc/g/pLtMj2.png'" /> -->
    <svg :style="style" :width="width" :height="width" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <path id="shapeSquircle" d="M50 25C50 43.4095 43.4095 50 25 50C6.59051 50 0 43.4095 0 25C0 6.59051 6.59051 0 25 0C43.4095 0 50 6.59051 50 25Z"></path>
        <clipPath id="clipSquircle">
          <use xlink:href="#shapeSquircle" />
        </clipPath>
      </defs>
      <image style="border: 10px solid #ffffff" width="100%" height="100%" preserveAspectRatio="xMidYMid slice" clip-path="url(#clipSquircle)" :xlink:href="imgCheck" />
    </svg>
    <svg v-if="radius == 1" :style="backStyle" width="78px" height="80px" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
      <image
        width="100%"
        height="100%"
        preserveAspectRatio="xMidYMid slice"
        clip-path="url(#clipSquircle)"
        xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAA1BMVEX///+nxBvIAAAASElEQVR4nO3BgQAAAADDoPlTX+AIVQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADwDcaiAAFXD1ujAAAAAElFTkSuQmCC"
      />
    </svg>
    <!-- 채팅방목록에서 멤버가 2명인경우 -->
    <svg v-if="radius == 3" :style="backStyle" width="3px" height="41px" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
      <image
        width="100%"
        height="100%"
        preserveAspectRatio="xMidYMid slice"
        clip-path="url(#clipSquircle)"
        xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAA1BMVEX///+nxBvIAAAASElEQVR4nO3BgQAAAADDoPlTX+AIVQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADwDcaiAAFXD1ujAAAAAElFTkSuQmCC"
      />
    </svg>
    <!-- 채팅방목록에서 멤버가 3명인경우 -->
    <svg v-if="radius == 4" :style="backStyle" width="3px" height="41px" viewBox="0 0 50 50" fill="none" xmlns="http://www.w3.org/2000/svg">
      <image
        width="100%"
        height="100%"
        preserveAspectRatio="xMidYMid slice"
        clip-path="url(#clipSquircle)"
        xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAA1BMVEX///+nxBvIAAAASElEQVR4nO3BgQAAAADDoPlTX+AIVQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADwDcaiAAFXD1ujAAAAAElFTkSuQmCC"
      />
    </svg>
  </div>
</template>

<script>
export default {
  name: "ProfileImg",
  data() {
    return {
      style: Object,
      backStyle: Object,
    };
  },
  props: {
    imgUrl: {
      type: String,
      default: "https://ifh.ccs/g/pLtMj2.png",
    },
    width: {
      type: String,
      default: "50px",
    },
    height: String,
    radius: {
      type: Number,
      default: 0,
    },
  },
  computed: {
    imgCheck() {
      if (this.imgUrl == null) {
        return "https://ifh.cc/g/pLtMj2.png";
      }
      return this.imgUrl;
    },
  },
  methods: {
    replaceImg(e) {
      e.target.src = "https://ifh.cc/g/pLtMj2.png";
    },
  },
  mounted() {
    if (this.radius > 0) {
      this.style = {
        "z-index": this.radius + 1,
        position: "absolute",
        top: "5px",
        left: "5px",
      };
      this.backStyle = {
        position: "absolute",
        "z-index": this.radius,
      };
    }
  },
};
</script>

<style scoped>
svg {
  pointer-events: none;
}
</style>
