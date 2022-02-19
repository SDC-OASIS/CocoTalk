import VueAxios from "axios";
import store from "@/store";
import router from "@/router";

const axios = VueAxios.create({
  // API GateWay 주소로 기본 설정
  baseURL: "https://cocotalk-api.ml/",
  headers: {
    "Content-type": "application/json",
  },
});

const excloudeURL = new Set(["auth/reissue"]);
//요청) 요청 직전 인자값 : axios config / 요청 에러 : error
axios.interceptors.request.use(
  function (config) {
    //access token이 필요없는 API를 제외하고 넣어줌
    if (!excloudeURL.has(config.url)) config.headers["X-ACCESS-TOKEN"] = store.getters["userStore/accessToken"];
    return config;
  },
  function (error) {
    console.log(error);
    return Promise.reject(error);
  },
);

//응답) 요청 직전 인자값 : axios config / 요청 에러 : error
// 응답 인터셉터 추가
axios.interceptors.response.use(
  // 응답 데이터를 가공
  function (response) {
    const request = response.config;
    // JWT 토큰이 만료됨
    let isTokenValid = !response.data.error || response.data.error.type != "JWT_AUTHENTICATION";
    let isReissueRq = request.url == "auth/reissue";
    // ACCESSTOKEN 만료일 경우 토큰 재발급 요청
    if (!isTokenValid && !isReissueRq) {
      return store
        .dispatch("userStore/reissue")
        .then(() => {
          console.log("[AXIOS] 토큰 재발급 성공, 기존 요청을 다시 시도합니다 : [시도할 요청]", request);
          return axios(request);
        })
        .catch((error) => {
          console.log("[AXIOS] 토큰 재발급이 실패했습니다. : [응답]", error.data);
          // REFRESH TOKEN 유효성 X
          store.dispatch("userStore/logout").then(() => {
            // 만료 알림 띄우기
            router.push("/login");
            const payload = {
              status: "open",
              text: "로그인이 만료되었습니다.",
            };
            store.dispatch("modal/openAlert", payload, { root: true });
            return Promise.reject("토큰 재발급 실패", error);
          });
        });
    }
    return response;
  },
  function (error) {
    // 오류 응답 처리
    return Promise.reject(error);
  },
);

export default axios;
