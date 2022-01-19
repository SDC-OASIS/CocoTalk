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

let room = null;
let roomId = null;

const domain = "http://localhost:8080/api/v1/chat";

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    userId = document.querySelector('#userId').value.trim();
    friendId = document.querySelector('#friendId').value.trim();

    if(userId) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/stomp');
        stompClient = Stomp.over(socket);
        // SockJS와 stomp client를 통해 연결을 시도.

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

async function onConnected() {
    try {
        // let friendid = Math.floor(Math.random() * 1000001) + 2;
        room = await axios.get(domain + "/rooms/private?userid=" + userId + "&friendid=" + friendId);
        if(room.data.data) {
            roomId = room.data.data.id;
            console.log('채팅방 존재 -> roomId = ' + roomId);
        } else {
            room = await axios.post(domain + "/rooms", {
                name: "RoomName",
                img: "img",
                type: 0,
                members: [
                    {
                        userId: userId,
                        isJoining: true,
                        accessedAt: "2022-01-15T11:34:06",
                        joinedAt: "2022-01-15T11:34:06"
                    },
                    {
                        userId: friendId,
                        isJoining: true,
                        accessedAt: "2022-01-15T11:34:06",
                        joinedAt: "2022-01-15T11:34:06"
                    }
                ],
                messageIds: [],
                noticeIds: []
            });
            roomId = room.data.data.id;
            console.log('채팅방 없음 -> 채팅방 생성 roomId = ' + roomId);
        }

        // friendName으로 토픽 구독
        stompClient.subscribe('/topic/' + roomId, onMessageReceived);

        let joinMessage = {
            roomId: roomId,
            userId: userId,
            type: 1,
            content: null,
            sentAt: getLocalDateTime()
        }

        console.log(joinMessage);

        // stompClient.send("/simple/chat/" + roomId + "/invite",
        //     {},
        //     JSON.stringify(joinMessage) // join
        // )
        connectingElement.classList.add('hidden');
    } catch(e) {
        alert(e);
    }
}


function onError(error) {
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

        stompClient.send("/simple/chat/" + roomId + "/send", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function invite (event) {
    const inviteeIds = document.querySelector('#inviteeId').value.split(",");
    if(inviteeIds && stompClient) {
        let inviteMessage = { // 일종의 joinMessage
            roomId: roomId,
            userId: inviteeIds,
            type: 1,
            content: null,
            sentAt: getLocalDateTime()
        }

        stompClient.send("/simple/chat/" + roomId + "/invite", {}, JSON.stringify(inviteMessage));
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

    stompClient.send("/simple/chat/" + roomId + "/leave", {}, JSON.stringify(leaveMessage));
    messageInput.value = '';

    chatPage.classList.add('hidden');
    usernamePage.classList.remove('hidden');

    const headers = {
    };
    if(!stompClient) stompClient.disconnect(() => {}, headers);

    event.preventDefault();
}


function onMessageReceived(payload) { // subscribe시 이 함수로 처리
    const message = JSON.parse(payload.body);

    const messageElement = document.createElement('li');

    if(message.type === 1) { // join
        messageElement.classList.add('event-message');
        message.content = message.userId + ' joined!';
    } else if (message.type === 2) { // leave
        messageElement.classList.add('event-message');
        message.content = message.userId + ' left!';
    } else if (message.type === 3) { // away
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

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
inviteForm.addEventListener('submit', invite, true)
leaveForm.addEventListener('submit', leave, true)
