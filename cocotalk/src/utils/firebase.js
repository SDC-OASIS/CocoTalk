import firebase from "firebase/app";
import "firebase/messaging";

// 서비스 워커 삭제 후 등록
navigator.serviceWorker.getRegistrations().then(function (registrations) {
  for (let registration of registrations) {
    console.log("기존 serviceWorker를 제거합니다.", registration);
    registration.unregister();
  }
  navigator.serviceWorker.register("/firebase-messaging-sw.js").then((registration) => {
    console.log("serviceWorker registration");
    return message.useServiceWorker(registration);
  });
});

const config = {
  apiKey: process.env.VUE_APP_FIRBASE_API_KEY,
  authDomain: process.env.VUE_APP_FIRBASE_AUTH_DOMAIN,
  projectId: process.env.VUE_APP_FIRBASE_PROJECT_ID,
  storageBucket: process.env.VUE_APP_FIRBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.VUE_APP_FIRBASE_MESSAGING_SENDER_ID,
  appId: process.env.VUE_APP_FIRBASE_API_ID,
  measurementId: process.env.VUE_APP_FIRBASE_MEASUREMENT_ID,
};

firebase.initializeApp(config);

// firebase.initializeApp(fireConfig);

const message = firebase.messaging();

async function deleteToken() {
  return new Promise((resolve) => {
    message
      .deleteToken()
      .then((res) => {
        resolve(res);
      })
      .catch(() => {
        resolve(false);
      });
  });
}
async function getToken() {
  let isDelete = await deleteToken();
  console.log("isDelete", isDelete);
  return new Promise((resolve, reject) => {
    message
      .requestPermission()
      .then(() => {
        console.log("Notification permission granted.");
        resolve(message.getToken());
      })
      .catch((err) => {
        console.log("[GET FCM TOKEN]", err);
        reject(err);
      });
  });
}
//포그라운드 메시지
//포그라운드 메시지는 사용하지 않을 예정
// message.onMessage(({ notification }) => {
//   const { title, body } = notification;
//   console.log("[PUSH] onMessage: ", `${title} ${body}`);
//   // alert(`${title} ${body}`);
//   const options = {
//     body: body,
//     icon: "/mococo.png", //s3처럼 따로 url 안쓰면 edge는 안보임
//     // icon: "/favicon.ico",
//   };
//   new Notification(title, options);
// });

export { getToken };
