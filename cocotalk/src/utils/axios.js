import VueAxios from "axios";
import store from "@/store";
import router from "@/router";

const axios = VueAxios.create({
  // API GateWay 주소로 기본 설정
  baseURL: "http://138.2.88.163:8000/",
  headers: {
    "Content-type": "application/json",
  },
});

//요청) 요청 직전 인자값 : axios config / 요청 에러 : error
axios.interceptors.request.use(
  function (config) {
    config.headers["X-ACCESS-TOKEN"] = store.getters["userStore/accessToken"];
    // config.headers["X-REFRESH-TOKEN"] = state.refreshToken;
    // console.log("헤더넣기 완료");
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
    let isExpire = response.data.error && response.data.error.type == "JWT_AUTHENTICATION";
    // isExpire = false;
    // 토큰 재발급 로직
    if (isExpire) {
      return store
        .dispatch("userStore/reissue")
        .then(() => {
          console.log("[axios] 토큰 재발급 성공, 기존 요청을 다시 시도합니다", request);
          return axios(request);
        })
        .catch((error) => {
          // refresh token 유효성 X
          console.error("[REFRESH TOKEN IS NOT VALID] 토큰 재발급 실패", error);
          store.dispatch("userStore/logout").then(() => {
            alert("로그인이 만료되었습니다");
            router.push("/login");
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
