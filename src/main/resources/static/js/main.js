'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const exitForm = document.querySelector('#exitForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let stompClient = null;
let username = null;
let friendName = null;
let myid = 1;

let room = null;

const domain = "http://localhost:8080/api/v1/chat";

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    friendName = document.querySelector('#friendName').value.trim();

    if(username) {
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
        let friendid = Math.floor(Math.random() * 1000001) + 2;
        room = await axios.get(domain + "/rooms/private?myid=" + myid + "&friendid=" + friendid);
        if(!room.data.data) {
            room = await axios.post(domain + "/rooms", {
                name: username + ", " + friendName,
                img: "img",
                type: 0,
                members: [
                    {
                        userId: 1,
                        isJoining: true,
                        accessedAt: "2022-01-15T11:34:06",
                        joinedAt: "2022-01-15T11:34:06"
                    },
                    {
                        userId: friendid,
                        isJoining: true,
                        accessedAt: "2022-01-15T11:34:06",
                        joinedAt: "2022-01-15T11:34:06"
                    }
                ],
                messagePk: [],
                noticePk: []
            });
        }
        console.log(room.data.data);

        let roomId = room.data.data.id;

        // friendName으로 토픽 구독
        stompClient.subscribe('/topic/' + roomId, onMessageReceived);

        // Tell your username to the server
        stompClient.send("/simple/chat.addUser/" + roomId,
            {},
            JSON.stringify({sender: username, type: 'JOIN'})
        )
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
    let roomId = room.data.data.id;

    const messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };

        stompClient.send("/simple/chat.sendMessage/" + roomId, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function exit(event) {
    var chatMessage = {
        sender: username,
        content: messageInput.value,
        type: 'LEAVE'
    };

    stompClient.send("/simple/chat.sendMessage/" + roomId, {}, JSON.stringify(chatMessage));
    messageInput.value = '';

    chatPage.classList.add('hidden');
    usernamePage.classList.remove('hidden');

    const headers = {
    };
    if(!stompClient) stompClient.disconnect(() => {}, headers);

    event.preventDefault();
}


function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    const messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('span');
        const usernameText = document.createTextNode(message.sender);
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

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
exitForm.addEventListener('submit', exit, true)