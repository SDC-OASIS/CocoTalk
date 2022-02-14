import firebase from "firebase/app";
import "firebase/messaging";
console.log("[main.js] initializeApp", firebase.initializeApp);
firebase.initializeApp({
  apiKey: "AIzaSyAVvM4t9bfI2HR007WmjEAoT4lmBGfS4LM",
  authDomain: "cocotalk-1cc7f.firebaseapp.com",
  projectId: "cocotalk-1cc7f",
  storageBucket: "cocotalk-1cc7f.appspot.com",
  messagingSenderId: "1046572361165",
  appId: "1:1046572361165:web:a27023ed3d8eeabb365084",
  measurementId: "G-GR9JM30497",
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
