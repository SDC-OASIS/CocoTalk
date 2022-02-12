import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store/index.js";
import InfiniteLoading from "vue-infinite-loading";
import VueMoment from "vue-moment";
import moment from "moment";

Vue.config.productionTip = false;
moment.locale("ko");
Vue.use(VueMoment, { moment });

new Vue({
  router,
  store,
  InfiniteLoading,
  render: (h) => h(App),
}).$mount("#app");
