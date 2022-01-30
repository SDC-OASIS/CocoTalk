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

let stompClient = null;
let userId = null;
let friendId = null;
let token = null;

let room = null;
let roomId = null;

let socket = null;

let headers = null;

const domain = "http://localhost:8083";

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
                    console.log('채팅방 존재 -> roomId = ' + roomId);

                    socket = new SockJS('/stomp');
                    stompClient = Stomp.over(socket);

                    console.log("enter connect");
                    stompClient.connect({
                        view: "chatroom",
                        userId: userId,
                        roomId: roomId
                    }, onConnected, onError);
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
    let now = getLocalDateTime();
    axios.post(domain + "/rooms", {
        name: "RoomName",
        img: "img",
        type: 0,
        memberIds: [userId, friendId],
    }, {headers}).then(res => {
        console.log(res);
        roomId = res.data.data.id;
        console.log('채팅방 생성됨 roomId = ' + roomId);

        socket = new SockJS('/stomp');
        stompClient = Stomp.over(socket);

        stompClient.connect({
            view : "chatroom",
            userId : userId,
            roomId : roomId
        }, onConnectAndSend, onError);

    }).catch(err => {
        alert(err);
    })
    event.preventDefault();
}

function onConnected() {
    messageForm.addEventListener('submit', sendMessage, true)
    stompClient.subscribe('/topic/' + roomId, onMessageReceived);
    connectingElement.classList.add('hidden');
}

function onConnectAndSend() {
    const messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        let chatMessage = {
            roomId: roomId,
            userId: userId,
            type: 0,
            content: messageInput.value,
            sentAt: getLocalDateTime()
        };

        stompClient.send("/simple/" + roomId + "/send", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }

    messageForm.removeEventListener('submit', createRoomAndConnectAndSend, true)
    onConnected();
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        let chatMessage = {
            roomId: roomId,
            userId: userId,
            type: 0,
            content: messageInput.value,
            sentAt: getLocalDateTime()
        };

        stompClient.send("/simple/" + roomId + "/send", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function invite (event) {
    const inviteeIds = document.querySelector('#inviteeId').value.split(",");
    if(inviteeIds && stompClient) {
        let inviteMessage = { // 일종의 joinMessage
            roomId: roomId,
            userId: userId,
            invitees: inviteeIds,
            type: 3,
            content: null,
            sentAt: getLocalDateTime()
        }

        stompClient.send("/simple/" + roomId + "/invite", {}, JSON.stringify(inviteMessage));
        inviteInput.value = '';
    }
    event.preventDefault();
}

function leave(event) {
    let leaveMessage = {
        roomId: roomId,
        userId: userId,
        type: 2,
        content: null,
        sentAt: getLocalDateTime()
    }

    stompClient.send("/simple/" + roomId + "/leave", {}, JSON.stringify(leaveMessage));
    messageInput.value = '';

    chatPage.classList.add('hidden');
    usernamePage.classList.remove('hidden');

    const headers = {
    };
    if(!stompClient) {
        stompClient.unsubscribe('/topic/' + roomId);
        stompClient.disconnect(() => {}, headers);
    }

    event.preventDefault();
}


function onMessageReceived(payload) { // subscribe시 이 함수로 처리
    console.log('onMessageReceived');
    console.log(payload);

    const message = JSON.parse(payload.body);

    const messageElement = document.createElement('li');

    if(message.type === 1) { // join
        messageElement.classList.add('event-message');
        message.content = message.userId + ' joined!';
    } else if (message.type === 2) { // leave
        messageElement.classList.add('event-message');
        message.content = message.userId + ' left!';
    } else if (message.type === 3) { // invite
        messageElement.classList.add('event-message');
        message.content = message.invitees + ' invited!';
    } else if (message.type === 4) { // away
        messageElement.classList.add('event-message');
        message.content = message.userId + ' away from keyboard!';
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