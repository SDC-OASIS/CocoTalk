'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const inviteForm = document.querySelector('#inviteForm');
const awakeForm = document.querySelector('#awakeForm');
const leaveForm = document.querySelector('#leaveForm');
const messageInput = document.querySelector('#message');
const inviteInput = document.querySelector('#inviteeId');
const awakeInput = document.querySelector('#awakeMessage');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let chatRoomSocket = null;
let chatListSocket = null;

let chatRoomClient = null;
let chatListClient = null;

let userId = null;
let friendId = null;
let token = null;

let room = null;
let roomId = null;
let messageBundleIds = null;
let nextMessageBundleId = null;
let currentMessageBundleCount = null;

let headers = null;

const domain = "http://localhost:8080"
// const domain = "http://138.2.93.111:8080"
// const domain = "http://138.2.88.163/chat"

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function enter(event) {
    userId = document.querySelector('#userId').value.trim();
    friendId = document.querySelector('#friendId').value.trim();
    token = document.querySelector('#token').value.trim();

    headers = {
        'X-ACCESS-TOKEN': token,
        'Accept': '*/*'
    }

    if (userId && friendId) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        console.log("userId, friendId 확인됨 -> 리스트 소켓 연결");

        chatListSocket = new SockJS('/stomp');
        chatListClient = Stomp.over(chatListSocket);

        chatListClient.connect({
            view: "chatList",
            userId: userId
        }, onConnectedListSocket, onError);

        axios.get(domain + "/rooms/private/" + friendId, {headers})
            .then(res => {
                console.log(res);
                if (!res.data.data.id) {
                    console.log('채팅방 없음');
                    console.log("createRoomAndConnectAndSend 이벤트 리스너에 등록됨");
                    messageForm.addEventListener('submit', createRoomAndConnectAndSend, true);
                } else {
                    roomId = res.data.data.id;
                    messageBundleIds = res.data.data.messageBundleIds
                    messageBundleIds = messageBundleIds.substring(1, messageBundleIds.length - 1).split(", ");
                    console.log('채팅방 존재 -> roomId = ' + roomId);
                    console.log("messageBundleIds = " + messageBundleIds);
                    nextMessageBundleId = messageBundleIds[messageBundleIds.length - 1];

                    chatRoomSocket = new SockJS(domain + '/stomp');
                    chatRoomClient = Stomp.over(chatRoomSocket);

                    console.log("enter connect");

                    chatRoomClient.connect({
                        view: "chatRoom",
                        userId: userId,
                        roomId: roomId
                    }, onConnectedRoomSocket, onError);
                }
            })
            .catch(err => {
                alert(err);
            })
        event.preventDefault();
    }
}

function createRoomAndConnectAndSend(event){
    console.log("createRoomAndConnectAndSend 이벤트 발생");

    const messageContent = messageInput.value.trim();

    if (messageContent) {
        let roomRequest = {
            roomName: "호로로로로롤ㄹㄹ로로로",
            img: "이미지",
            type: 0,
            members: [
                {
                    userId: userId,
                    username: "황종훈",
                    profile: JSON.stringify({
                        profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
                        background: "https://ifh.cc/g/CgiChn.jpg",
                        message: "화이팅화이팅"
                    })
                },
                {
                    userId: friendId,
                    username: "이희은",
                    profile: JSON.stringify({
                        profile: "https://media.bunjang.co.kr/product/150007679_1_1616845509_w360.jpg",
                        background: "https://ifh.cc/g/CgiChn.jpg",
                        message: "화이팅화이팅"
                    })
                }
            ]
        }
        chatListClient.send("/simple/chatroom/new", {}, JSON.stringify(roomRequest));
        event.preventDefault();
    }
}

function onConnectedListSocket() {
    console.log("onConnectedListSocket 호출됨")
    console.log(userId);
    chatListClient.subscribe('/topic/' + userId + '/message', onMessageReceivedListSocket);
    chatListClient.subscribe('/topic/' + userId + '/room', onRoomReceivedListSocket);
    chatListClient.subscribe('/topic/' + userId + '/room/new', onNewRoomReceivedListSocket);
}

function onConnectedRoomSocket() {
    console.log("onConnectedRoomSocket 호출됨");
    console.log("sendMessage submit에 이벤트 리스너 등록됨");
    messageForm.addEventListener('submit', sendMessage, true);
    chatRoomClient.subscribe('/topic/' + roomId + '/message', onMessageReceivedRoomSocket); // /chatroom/topic?
    connectingElement.classList.add('hidden');
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    console.log("sendMessage 이벤트 발생")

    const messageContent = messageInput.value.trim();

    console.log("nextMessageBundleId = " + nextMessageBundleId);

    if(messageContent && chatRoomClient) {
        let chatMessage = {
            roomId: roomId,
            userId: userId,
            messageBundleId: nextMessageBundleId,
            type: 0,
            content: messageInput.value
        };

        chatRoomClient.send("/simple/chatroom/" + roomId + "/message/send", {}, JSON.stringify(chatMessage));
        // chatListClient.send("/simple/chatlist/" + userId + "/send", {}, JSON.stringify(chatMessage)); // 자기 자신에게는 메시지 보내지 않음
        messageInput.value = '';
    }
    event.preventDefault();
}

function invite (event) {
    const inviteeIds = document.querySelector('#inviteeId').value.split(",");
    let invitees = [];
    for(let i = 0; i < inviteeIds.length; ++i) {
        console.log(inviteeIds[i]);
        invitees.push({
            userId: inviteeIds[i],
            username: "유저" + inviteeIds[i],
            profile: "profile"
        })
    }
    if(inviteeIds && chatRoomClient) {
        let inviteMessage = { // 일종의 joinMessage
            roomId: roomId,
            userId: userId,
            invitees: invitees,
            messageBundleId: nextMessageBundleId,
            type: 3,
            content: userId + '가 입장했습니다.'
        }
        chatRoomClient.send("/simple/chatroom/" + roomId + "/message/invite", {}, JSON.stringify(inviteMessage));
        inviteInput.value = '';
    }
    event.preventDefault();
}

function awake(event) {
    const messageContent = awakeInput.value.trim();
    if(messageContent && chatRoomClient) {
        let awakeMessage = {
            roomId: roomId,
            userId: userId,
            messageBundleId: nextMessageBundleId,
            type: 5,
            content: messageContent
        }

        chatRoomClient.send("/simple/chatroom/" + roomId + "/message/awake", {}, JSON.stringify(awakeMessage));
        inviteInput.value = '';
    }
    event.preventDefault();
}

function leave(event) {
    let leaveMessage = {
        roomId: roomId,
        userId: userId,
        messageBundleId: nextMessageBundleId,
        type: 2,
        content: userId + '가 나갔습니다.',
        // sentAt: getLocalDateTime()
    }

    chatRoomClient.send("/simple/chatroom/" + roomId + "/message/leave", {}, JSON.stringify(leaveMessage));
    messageInput.value = '';

    chatPage.classList.add('hidden');
    usernamePage.classList.remove('hidden');

    const headers = {
        action: 'leave'
    };
    if(chatRoomClient) {
        // chatListClient.unsubscribe('/topic/' + userId + '/message');
        // chatListClient.unsubscribe('/topic/' + userId + '/room');
        // chatListClient.unsubscribe('/topic/' + userId + '/room/new');

        chatRoomClient.unsubscribe(domain + '/topic/' + roomId + '/message');
        chatRoomClient.unsubscribe(domain + '/topic/' + roomId + '/room');

        chatRoomClient.disconnect(() => {}, headers);
        // chatListClient.disconnect(() => {}, {});
    }

    event.preventDefault();
}


function onMessageReceivedRoomSocket(payload) { // subscribe시 이 함수로 처리
    console.log('onMessageReceivedRoomSocket');
    const body = JSON.parse(payload.body);

    console.log(payload);

    const message = body.message;
    const bundleInfo = body.bundleInfo;
    currentMessageBundleCount = bundleInfo.currentMessageBundleCount;
    nextMessageBundleId = bundleInfo.nextMessageBundleId;

    const messageElement = document.createElement('li');

    if(message.type === 1) { // join
        messageElement.classList.add('event-message');
        // message.content = message.userId + ' joined!';
    } else if (message.type === 2) { // leave
        messageElement.classList.add('event-message');
        // message.content = message.userId + ' left!';
    } else if (message.type === 3) { // invite
        messageElement.classList.add('event-message');
        // message.content = message.inviteeIds + ' invited!';
    } else if (message.type === 4) { // away
        messageElement.classList.add('event-message');
        // message.content = message.userId + ' away from keyboard!';
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.userId);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.userId);

        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('span');
        const usernameText = document.createTextNode(message.userId);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

// function onRoomReceivedRoomSocket(payload) {
//     console.log('onRoomReceivedRoomSocket');
// }

function onMessageReceivedListSocket(payload) {
    console.log('onMessageReceivedListSocket');
    // console.log(payload);
}

function onRoomReceivedListSocket(payload) {
    console.log('onRoomReceivedListSocket');
    // console.log(payload);
}

function onNewRoomReceivedListSocket(payload) {
    console.log('onRoomReceivedListSocket');
    console.log(payload);

    const body = JSON.parse(payload.body);
    roomId = body.id;
    console.log('채팅방 생성됨 roomId = ' + roomId);

    messageBundleIds = body.messageBundleIds
    messageBundleIds = messageBundleIds.substring(1, messageBundleIds.length - 1).split(", ");
    console.log("messageBundleIds = " + messageBundleIds);
    nextMessageBundleId = messageBundleIds[messageBundleIds.length - 1];

    chatRoomSocket = new SockJS(domain + '/stomp');
    chatRoomClient = Stomp.over(chatRoomSocket);

    chatRoomClient.connect({
        view : "chatRoom",
        userId : userId,
        roomId : roomId
    }, onConnectAndSend, onError);

    console.log("createRoomAndConnectAndSend() submit에 이벤트 리스너 제거됨")
    messageForm.removeEventListener('submit', createRoomAndConnectAndSend, true)
}

function onConnectAndSend() {
    const messageContent = messageInput.value.trim();

    if(messageContent && chatRoomClient) {
        let chatMessageRequest = {
            roomId: roomId,
            userId: userId,
            messageBundleId: nextMessageBundleId,
            type: 0,
            content: messageInput.value
        };

        chatRoomClient.send("/simple/chatroom/" + roomId + "/message/send", {}, JSON.stringify(chatMessageRequest));
        messageInput.value = '';
    }

    console.log("onConnectAndSend() 호출됨")
    console.log("createRoomAndConnectAndSend() submit에 이벤트 리스너 제거됨")
    messageForm.removeEventListener('submit', createRoomAndConnectAndSend, true)
    onConnectedRoomSocket();
}


function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    const index = Math.abs(hash % colors.length);
    return colors[index];
}

function getLocalDateTime() {
    let today = new Date();

    let year = today.getFullYear();
    let month = ('0' + (today.getMonth() + 1)).slice(-2);
    let day = ('0' + today.getDate()).slice(-2);

    let hours = ('0' + today.getHours()).slice(-2);
    let minutes = ('0' + today.getMinutes()).slice(-2);
    let seconds = ('0' + today.getSeconds()).slice(-2);

    return year + '-' + month  + '-' + day + "T" + hours + ':' + minutes  + ':' + seconds
}

usernameForm.addEventListener('submit', enter, true)
inviteForm.addEventListener('submit', invite, true)
leaveForm.addEventListener('submit', leave, true)
awakeForm.addEventListener('submit', awake, true)