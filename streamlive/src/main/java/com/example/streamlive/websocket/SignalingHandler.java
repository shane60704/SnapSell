package com.example.streamlive.websocket;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class SignalingHandler extends TextWebSocketHandler {
    private Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private Map<String, WebSocketSession> broadcasters = new ConcurrentHashMap<>();
    private Map<String, RoomInfo> roomInfos = new ConcurrentHashMap<>(); // 用於保存房間資訊

    private Set<WebSocketSession> lobbySessions = Collections.synchronizedSet(new HashSet<>()); // 大廳連線

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JSONObject jsonMessage = new JSONObject(message.getPayload());
        String type = jsonMessage.getString("type");

        switch (type) {
            case "create":
                String createRoomId = jsonMessage.getString("roomId");
                createRoom(createRoomId, session);
                break;
            case "join":
                String joinRoomId = jsonMessage.getString("roomId");
                joinRoom(joinRoomId, session);
                break;
            case "offer":
            case "answer":
            case "candidate":
                String roomId = jsonMessage.getString("roomId");
                forwardMessage(roomId, session, jsonMessage);
                break;
            case "chat":
                String chatRoomId = jsonMessage.getString("roomId");
                broadcastChat(chatRoomId, session, jsonMessage.getString("content"));
                break;
            case "getRoomList":
                sendRoomList(session);
                break;
            case "lobby":
                lobbySessions.add(session);
                sendRoomList(session);
                break;
        }
    }

    private void createRoom(String roomId, WebSocketSession session) {
        rooms.put(roomId, new CopyOnWriteArrayList<>());
        broadcasters.put(roomId, session);
        roomInfos.put(roomId, new RoomInfo(roomId, Instant.now(), 0)); // 添加房間資訊
        sendToSession(session, new JSONObject().put("type", "created").toString());
        broadcastRoomList(); // 更新大廳的房間清單
    }

    private void joinRoom(String roomId, WebSocketSession session) throws IOException {
        if (!rooms.containsKey(roomId)) {
            sendToSession(session, new JSONObject().put("type", "error").put("message", "房間不存在").toString());
            return;
        }
        rooms.get(roomId).add(session);

        // 分配唯一的 viewerId
        String viewerId = session.getId();

        // 更新房間資訊中的觀眾人數
        RoomInfo roomInfo = roomInfos.get(roomId);
        roomInfo.incrementViewerCount();

        // 發送 'joined' 消息，包含 viewerId
        sendToSession(session, new JSONObject().put("type", "joined").put("viewerId", viewerId).toString());

        // 通知主播，包含 viewerId
        WebSocketSession broadcaster = broadcasters.get(roomId);
        sendToSession(broadcaster, new JSONObject().put("type", "newViewer").put("viewerId", viewerId).toString());

        broadcastRoomList(); // 更新大廳的房間清單
    }

    private void forwardMessage(String roomId, WebSocketSession sender, JSONObject message) throws IOException {
        String viewerId = message.optString("viewerId");

        if (sender == broadcasters.get(roomId)) {
            // 發送者是主播，發送給特定的觀眾
            WebSocketSession recipient = null;
            for (WebSocketSession viewer : rooms.get(roomId)) {
                if (viewer.getId().equals(viewerId)) {
                    recipient = viewer;
                    break;
                }
            }
            if (recipient != null) {
                sendToSession(recipient, message.toString());
            }
        } else {
            // 發送者是觀眾，發送給主播
            WebSocketSession recipient = broadcasters.get(roomId);
            sendToSession(recipient, message.toString());
        }
    }

    private void broadcastChat(String roomId, WebSocketSession sender, String content) throws IOException {
        JSONObject chatMessage = new JSONObject()
                .put("type", "chat")
                .put("content", content)
                .put("sender", sender == broadcasters.get(roomId) ? "主播" : "觀眾");

        for (WebSocketSession session : rooms.get(roomId)) {
            sendToSession(session, chatMessage.toString());
        }
        sendToSession(broadcasters.get(roomId), chatMessage.toString());
    }

    private void sendToSession(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            // 處理發送消息時的異常
            e.printStackTrace();
        }
    }

    private void sendRoomList(WebSocketSession session) {
        List<JSONObject> roomList = new ArrayList<>();
        for (RoomInfo roomInfo : roomInfos.values()) {
            JSONObject roomJson = new JSONObject()
                    .put("roomId", roomInfo.getRoomId())
                    .put("startTime", roomInfo.getStartTime().toString())
                    .put("viewerCount", roomInfo.getViewerCount());
            roomList.add(roomJson);
        }
        JSONObject message = new JSONObject()
                .put("type", "roomList")
                .put("rooms", roomList);
        sendToSession(session, message.toString());
    }

    private void broadcastRoomList() {
        for (WebSocketSession session : lobbySessions) {
            sendRoomList(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        lobbySessions.remove(session);

        String roomIdToRemove = null;

        for (Map.Entry<String, WebSocketSession> entry : broadcasters.entrySet()) {
            if (entry.getValue() == session) {
                String roomId = entry.getKey();
                rooms.get(roomId).forEach(viewer -> {
                    try {
                        sendToSession(viewer, new JSONObject().put("type", "broadcasterLeft").toString());
                        viewer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                rooms.remove(roomId);
                broadcasters.remove(roomId);
                roomInfos.remove(roomId); // 移除房間資訊
                roomIdToRemove = roomId;
                break;
            }
        }

        if (roomIdToRemove != null) {
            broadcastRoomList(); // 更新大廳的房間清單
            return;
        }

        for (Map.Entry<String, List<WebSocketSession>> entry : rooms.entrySet()) {
            List<WebSocketSession> viewers = entry.getValue();
            if (viewers.remove(session)) {
                // 更新房間資訊中的觀眾人數
                RoomInfo roomInfo = roomInfos.get(entry.getKey());
                roomInfo.decrementViewerCount();
                broadcastRoomList(); // 更新大廳的房間清單
                break;
            }
        }
    }

    // 房間資訊的內部類別
    private static class RoomInfo {
        private String roomId;
        private Instant startTime;
        private int viewerCount;

        public RoomInfo(String roomId, Instant startTime, int viewerCount) {
            this.roomId = roomId;
            this.startTime = startTime;
            this.viewerCount = viewerCount;
        }

        public String getRoomId() {
            return roomId;
        }

        public Instant getStartTime() {
            return startTime;
        }

        public int getViewerCount() {
            return viewerCount;
        }

        public void incrementViewerCount() {
            viewerCount++;
        }

        public void decrementViewerCount() {
            viewerCount--;
        }
    }
}

