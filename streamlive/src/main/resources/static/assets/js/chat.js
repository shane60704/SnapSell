let stompClient = null;
let currentChatRoomId = null;
let currentSubscription = null; // 儲存當前的訂閱

// 連接 WebSocket 伺服器
function connectWebSocket() {
    const socket = new SockJS('/chat'); // 確認 WebSocket 端點為 /chat
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('已連接: ' + frame);
        const userId = getCurrentUserId(); // 從 localStorage 取得目前登入的用戶 ID
        if (userId) {
            subscribeToNewChatRoomNotifications(userId);
            loadUserChatRooms(userId); // 加載用戶的聊天室清單
        } else {
            alert("請先登入");
        }
    }, function(error) {
        console.error('連接失敗:', error);
        setTimeout(connectWebSocket, 5000); // 5 秒後重試
    });
}

// 訂閱新的聊天室通知
function subscribeToNewChatRoomNotifications(userId) {
    stompClient.subscribe(`/topic/newRoom/${userId}`, function(chatRoomMessage) {
        const chatRoom = JSON.parse(chatRoomMessage.body);
        console.log("收到新聊天室通知:", chatRoom);
        addChatRoomToList(chatRoom);
    });
}

// 訂閱特定聊天室
function subscribeToChatRoom(chatRoomId) {
    if (stompClient) {
        if (currentSubscription) {
            currentSubscription.unsubscribe(); // 正確取消訂閱
        }

        // 訂閱新的聊天室頻道
        currentSubscription = stompClient.subscribe(`/topic/chat/${chatRoomId}`, function(messageOutput) {
            const message = JSON.parse(messageOutput.body);
            displayMessage(message, true); // 新訊息觸發自動滾動
        });

        currentChatRoomId = chatRoomId;
    }
}

// 加載用戶的聊天室清單
function loadUserChatRooms(userId) {
    fetch(`/api/1.0/users/${userId}/chatrooms`)
        .then(response => response.json())
        .then(chatRooms => {
            chatRooms.forEach(chatRoom => {
                addChatRoomToList(chatRoom);
            });
        })
        .catch(error => console.error('加載聊天室清單失敗:', error));
}

// 顯示聊天室列表中的新聊天室
function addChatRoomToList(chatRoom) {
    const chatRoomList = document.getElementById("chatRoomList");
    if (!document.querySelector(`li[data-chat-room-id="${chatRoom.id}"]`)) {
        const listItem = document.createElement("li");
        listItem.textContent = chatRoom.uniqueChatroom;
        listItem.dataset.chatRoomId = chatRoom.id;
        listItem.onclick = function() {
            joinChatRoom(chatRoom.id, chatRoom.uniqueChatroom);
        };
        chatRoomList.appendChild(listItem);
    }
}

// 加入聊天室並訂閱
function joinChatRoom(chatRoomId, chatRoomName) {
    console.log("加入聊天室:", chatRoomId);
    document.getElementById('currentChatRoomName').textContent = chatRoomName; // 更新聊天室名稱
    subscribeToChatRoom(chatRoomId);
    connectWebSocketForContract(chatRoomName);
    loadChatHistory(chatRoomId); // 加載該聊天室的歷史訊息
}

// 加載聊天歷史記錄
function loadChatHistory(chatRoomId) {
    fetch(`/api/1.0/chatrooms/${chatRoomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            const messageArea = document.getElementById("messageArea");
            messageArea.innerHTML = ''; // 清空之前的訊息
            messages.forEach(message => {
                displayMessage(message, false); // 加載歷史訊息不滾動
            });
            // 在加載歷史訊息後，將視窗滾動到最新的訊息
            messageArea.scrollTop = messageArea.scrollHeight;
        })
        .catch(error => console.error('加載聊天歷史失敗:', error));
}

// 顯示訊息
function displayMessage(message, shouldScroll) {
    const messageArea = document.getElementById("messageArea");
    const messageWrapper = document.createElement("div");
    messageWrapper.classList.add("message-wrapper");

    // 判斷訊息發送者，將訊息顯示在不同的位置
    const userId = getCurrentUserId();
    if (message.senderId == userId) {
        messageWrapper.classList.add("right");  // 自己發送的訊息顯示在右邊
    } else {
        messageWrapper.classList.add("left");   // 對方的訊息顯示在左邊
    }

    const messageElement = document.createElement("div");
    messageElement.classList.add("message");
    messageElement.textContent = `${message.content}`;

    const timestampElement = document.createElement("div");
    timestampElement.classList.add("timestamp");
    timestampElement.textContent = formatTimestamp(message.timestamp);

    messageWrapper.appendChild(messageElement);
    messageWrapper.appendChild(timestampElement);
    messageArea.appendChild(messageWrapper);

    // 當是新訊息時才滾動到最新
    if (shouldScroll) {
        messageWrapper.scrollIntoView({ behavior: 'smooth', block: 'end' });
    }
}

// 發送訊息
document.getElementById("sendMessageBtn").addEventListener("click", function() {
    const messageInput = document.getElementById("messageInput");
    const messageContent = messageInput.value.trim();
    const userId = getCurrentUserId();
    if (messageContent && stompClient && currentChatRoomId) {
        const message = {
            chatRoomId: currentChatRoomId,   // 當前聊天室 ID
            senderId: userId,                // 發送者 ID (從 localStorage 獲取)
            content: messageContent,         // 訊息內容
            timestamp: new Date().toISOString() // 當前時間
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));  // 透過 STOMP 發送訊息到後端
        messageInput.value = ''; // 清空輸入框
    } else {
        alert("請選擇聊天室並輸入訊息");
    }
});

// 新增聊天室按鈕
document.getElementById("createChatRoomBtn").addEventListener("click", function() {
    const user2Id = prompt("請輸入要聊天的用戶 ID:");
    const user1Id = getCurrentUserId();
    if (user2Id && user1Id) {
        fetch(`/api/1.0/chatroom?user1Id=${user1Id}&user2Id=${user2Id}`, {
            method: 'POST'
        })
            .then(response => response.json())
            .then(chatRoom => {
                console.log("創建或獲取聊天室成功:", chatRoom);
                addChatRoomToList(chatRoom);
            })
            .catch(error => console.error('創建聊天室失敗:', error));
    } else {
        alert("請輸入有效的用戶 ID");
    }
});

// 從 localStorage 獲取當前登入的用戶 ID
function getCurrentUserId() {
    return localStorage.getItem('userId'); // 假設登入後 userId 存儲於 localStorage
}

// 格式化時間戳
function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    return `${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`; // 不顯示秒，確保分鐘兩位數
}

// 初始化 WebSocket 連接
connectWebSocket();