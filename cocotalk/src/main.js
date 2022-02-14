import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store/index.js";
import moment from "moment";
import VueMoment from "vue-moment";
import InfiniteLoading from "vue-infinite-loading";

Vue.config.productionTip = false;
moment.locale("ko");
Vue.use(VueMoment, { moment });

import "@/utils/firebase";

new Vue({
  router,
  store,
  InfiniteLoading,
  render: (h) => h(App),
}).$mount("#app");
