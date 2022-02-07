import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store/index.js";
import InfiniteLoading from "vue-infinite-loading";

Vue.config.productionTip = false;

new Vue({
	router,
	store,
	InfiniteLoading,
	render: (h) => h(App),
}).$mount("#app");
