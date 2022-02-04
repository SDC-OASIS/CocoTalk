'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const inviteForm = document.querySelector('#inviteForm');
const inviteInput = document.querySelector('#inviteeId');
const leaveForm = document.querySelector('#leaveForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let chatRoomClient = null;
// let chatListClient = null;
let userId = null;
let friendId = null;
let token = null;

let room = null;
let roomId = null;
let messageBundleIds = null;
let recentBundleId = null;

let chatRoomSocket = null;
// let chatListSocket = null;

let headers = null;

const domain = "http://localhost:8080";

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

        axios.get(domain + "/rooms/private/" + friendId, {headers})
            .then(res => {
                console.log(res);

                console.log("enter roomId = " + roomId);

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
                    recentBundleId = messageBundleIds[messageBundleIds.length - 1];

                    chatRoomSocket = new SockJS('/stomp');
                    // chatListSocket = new SockJS('/stomp');

                    chatRoomClient = Stomp.over(chatRoomSocket);
                    // chatListClient = Stomp.over(chatListSocket);

                    console.log("enter connect");

                    chatRoomClient.connect({
                        view: "chatRoom",
                        userId: userId,
                        roomId: roomId
                    }, onConnected, onError);

                    // chatListClient.connect({
                    //     view: "chatList",
                    //     userId: userId
                    // }, onConnected, onError);
                }
            })
            .catch(err => {
                alert(err);
            })
        event.preventDefault();
    }
}

function createRoomAndConnectAndSend(event){
    console.log("createRoomAndConnect 이벤트 발생");
    axios.post(domain + "/rooms", {
        name: "RoomName",
        img: "img",
        type: 0,
        memberIds: [userId, friendId],
    }, {headers}).then(res => {
        console.log(res);
        roomId = res.data.data.id;
        console.log('채팅방 생성됨 roomId = ' + roomId);

        messageBundleIds = res.data.data.messageBundleIds
        messageBundleIds = messageBundleIds.substring(1, messageBundleIds.length - 1).split(", ");
        console.log("messageBundleIds = " + messageBundleIds);
        recentBundleId = messageBundleIds[messageBundleIds.length - 1];

        chatRoomSocket = new SockJS('/stomp');
        // chatListSocket = new SockJS('/stomp');

        chatRoomClient = Stomp.over(chatRoomSocket);
        // chatListClient = Stomp.over(chatListSocket);

        chatRoomClient.connect({
            view : "chatRoom",
            userId : userId,
            roomId : roomId
        }, onConnectAndSend, onError);

        // chatListClient.connect({
        //     view : "chatList",
        //     userId : userId
        // }, onConnectAndSend, onError);

    }).catch(err => {
        alert(err);
    })
    event.preventDefault();
}

function onConnected() {
    console.log("onConnected 호출됨")
    console.log("sendMessage submit에 이벤트 리스너 등록됨")
    messageForm.addEventListener('submit', sendMessage, true)
    chatRoomClient.subscribe('/topic/' + roomId + '/message', onMessageReceived); // /chatroom/topic?
    // chatListClient.subscribe('/topic/' + userId, onMessageReceived);
    connectingElement.classList.add('hidden');
}

function onConnectAndSend() {
    const messageContent = messageInput.value.trim();

    if(messageContent && chatRoomClient) {
        let chatMessage = {
            roomId: roomId,
            userId: userId,
            messageBundleId: recentBundleId,
            type: 0,
            content: messageInput.value
            // sentAt: getLocalDateTime()
        };

        chatRoomClient.send("/simple/chatroom/" + roomId + "/message/send", {}, JSON.stringify(chatMessage));
        // chatListClient.send("/simple/chatlist/" + userId + "/send", {}, JSON.stringify(chatMessage)); // 자기 자신에게는 메시지 보내지 않음
        messageInput.value = '';
    }

    console.log("onConnectAndSend() 호출됨")
    console.log("createRoomAndConnectAndSend() submit에 이벤트 리스너 제거됨")
    messageForm.removeEventListener('submit', createRoomAndConnectAndSend, true)
    onConnected();
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    console.log("sendMessage 이벤트 발생")

    const messageContent = messageInput.value.trim();

    console.log("recentBundleId = " + recentBundleId);

    if(messageContent && chatRoomClient) {
        let chatMessage = {
            roomId: roomId,
            userId: userId,
            messageBundleId: recentBundleId,
            type: 0,
            content: messageInput.value
            // sentAt: getLocalDateTime()
        };

        chatRoomClient.send("/simple/chatroom/" + roomId + "/message/send", {}, JSON.stringify(chatMessage));
        // chatListClient.send("/simple/chatlist/" + userId + "/send", {}, JSON.stringify(chatMessage)); // 자기 자신에게는 메시지 보내지 않음
        messageInput.value = '';
    }
    event.preventDefault();
}

function invite (event) {
    const inviteeIds = document.querySelector('#inviteeId').value.split(",");
    if(inviteeIds && chatRoomClient) {
        let inviteMessage = { // 일종의 joinMessage
            roomId: roomId,
            userId: userId,
            inviteeIds: inviteeIds,
            messageBundleId: recentBundleId,
            type: 3,
            content: userId + '가 입장했습니다.'
            // sentAt: getLocalDateTime()
        }

        chatRoomClient.send("/simple/chatroom/" + roomId + "/message/invite", {}, JSON.stringify(inviteMessage));
        inviteInput.value = '';
    }
    event.preventDefault();
}

function leave(event) {
    let leaveMessage = {
        roomId: roomId,
        userId: userId,
        messageBundleId: recentBundleId,
        type: 2,
        content: userId + '가 나갔습니다.',
        // sentAt: getLocalDateTime()
    }

    chatRoomClient.send("/simple/chatroom/" + roomId + "/message/leave", {}, JSON.stringify(leaveMessage));
    messageInput.value = '';

    chatPage.classList.add('hidden');
    usernamePage.classList.remove('hidden');

    const headers = {
        action: 'left'
    };
    if(chatRoomClient) {
        chatRoomClient.unsubscribe('/topic/' + roomId + '/message');
        // chatListClient.unsubscribe('/topic/' + userId);

        chatRoomClient.disconnect(() => {}, headers);
        // chatListClient.disconnect(() => {}, {});
    }

    event.preventDefault();
}


function onMessageReceived(payload) { // subscribe시 이 함수로 처리
    console.log('onMessageReceived');
    console.log(payload);

    const body = JSON.parse(payload.body);
    const message = body.chatMessage;
    recentBundleId = body.bundleId.nextMessageBundleId;
    console.log(message);
    console.log(recentBundleId);

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