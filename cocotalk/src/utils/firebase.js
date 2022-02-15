import firebase from "firebase/app";
import "firebase/messaging";
firebase.initializeApp({
  apiKey: process.env.VUE_APP_FIRBASE_API_KEY,
  authDomain: process.env.VUE_APP_FIRBASE_AUTH_DOMAIN,
  projectId: process.env.VUE_APP_FIRBASE_PROJECT_ID,
  storageBucket: process.env.VUE_APP_FIRBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.VUE_APP_FIRBASE_MESSAGING_SENDER_ID,
  appId: process.env.VUE_APP_FIRBASE_API_ID,
  measurementId: process.env.VUE_APP_FIRBASE_MEASUREMENT_ID,
});
const message = firebase.messaging();
navigator.serviceWorker.register("/firebase-messaging-sw.js").then((registration) => {
  console.log("serviceWorker registration");
  return message.useServiceWorker(registration);
});

function getToken() {
  return new Promise((resolve, reject) => {
    message
      .requestPermission()
      .then(() => {
        console.log("Notification permission granted.");
        return message.deleteToken();
      })
      .then((isDelete) => {
        console.log("FCMTokeon Deleted", isDelete);
        resolve(message.getToken());
      })
      .catch((err) => {
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
