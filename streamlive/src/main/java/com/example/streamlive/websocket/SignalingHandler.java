package com.example.streamlive.websocket;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.dao.user.UserDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalingHandler extends TextWebSocketHandler {

    private final LiveStreamDao liveStreamDao;
    private final UserDao userDao;

    private Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private Map<String, WebSocketSession> broadcasters = new ConcurrentHashMap<>();
    private Map<String, RoomInfo> roomInfos = new ConcurrentHashMap<>(); // 保存房間資訊
    private Map<String, Integer> roomViewerCount = new HashMap<>(); // 記錄每個房間的累積觀眾人數
    private Set<WebSocketSession> lobbySessions = Collections.synchronizedSet(new HashSet<>()); //大廳連線

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JSONObject jsonMessage = new JSONObject(message.getPayload());
        String type = jsonMessage.getString("type");
        switch (type) {
            case "create":
                String createRoomId = jsonMessage.getString("roomId");
                String userId = jsonMessage.getString("userId");
                String title = jsonMessage.getString("title");
                String description = jsonMessage.getString("description");
                liveStreamDao.createLiveStreamRecord(userId, createRoomId);
                String userImage = userDao.getUserImageById(userId);
                log.info("----------:"+userImage);
                JSONArray productsArray = jsonMessage.getJSONArray("products");
                List<String> productsList = new ArrayList<>();
                for (int i = 0; i < productsArray.length(); i++) {
                    productsList.add(productsArray.getString(i));
                }
                createRoom(createRoomId,userImage,title, description, session, productsList);
                break;
            case "join":
                String joinRoomId = jsonMessage.getString("roomId");
                onViewerJoined(joinRoomId);
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

    private void createRoom(String roomId, String userImage, String title , String description, WebSocketSession session, List<String> products) {
        rooms.put(roomId, new CopyOnWriteArrayList<>());
        broadcasters.put(roomId, session);
        roomInfos.put(roomId, new RoomInfo(roomId,userImage,title,description,Instant.now(), 0, products));
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

        // 通知主播、觀眾 直播間的觀眾人數需要更動
        broadcastViewerCount(roomId);


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
                    .put("title",roomInfo.getTitle())
                    .put("userImage", roomInfo.getUserImage())
                    .put("description",roomInfo.getDescription())
                    .put("startTime", roomInfo.getStartTime().toString())
                    .put("viewerCount", roomInfo.getViewerCount())
                    .put("products", roomInfo.getProducts());
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
                Integer viewerCount = roomViewerCount.get(roomId);
                Map<String,Object> liveStatistics = liveStreamDao.getTotalPriceAndQuantity(roomId);
                BigDecimal totalPriceDecimal = (BigDecimal) liveStatistics.get("totalPrice");
                BigDecimal totalQuantityDecimal = (BigDecimal) liveStatistics.get("totalQuantity");

                int totalPrice = totalPriceDecimal != null ? totalPriceDecimal.intValue() : 0;
                int totalQuantity = totalQuantityDecimal != null ? totalQuantityDecimal.intValue() : 0;
                if (viewerCount == null){
                    liveStreamDao.updateLiveStreamRecord(roomId,0,totalQuantity,totalPrice);
                }else{
                    liveStreamDao.updateLiveStreamRecord(roomId,viewerCount,totalQuantity,totalPrice);
                }
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
                // 找到離開觀眾所在的房間
                String roomId = entry.getKey();
                RoomInfo roomInfo = roomInfos.get(roomId);
                // 更新房間資訊中的觀眾人數
                roomInfo.decrementViewerCount();

                // 通知主播、觀眾 需要更新在線人數
                onViewerLeft(roomId);

                // 通知主播有觀眾離開
                WebSocketSession broadcaster = broadcasters.get(roomId);
                if (broadcaster != null && broadcaster.isOpen()) {
                    JSONObject message = new JSONObject()
                            .put("type", "viewerLeft")
                            .put("viewerId", session.getId());

                    sendToSession(broadcaster, message.toString());
                }
                // 更新大廳的房間清單
                broadcastRoomList();
                break;
            }
        }
    }

    // 當有觀眾加入房間時調用此方法
    public void onViewerJoined(String roomId) {
        // 如果房間之前沒有累積人數，初始化為0
        roomViewerCount.putIfAbsent(roomId, 0);

        // 每次有新的觀眾連線，累積人數加1
        roomViewerCount.put(roomId, roomViewerCount.get(roomId) + 1);

        System.out.println("房間 " + roomId + " 的累積觀眾人數: " + roomViewerCount.get(roomId));
    }

    private void onViewerLeft(String roomId) {
        // 減少房間累積的觀眾人數
        if (roomViewerCount.containsKey(roomId)) {
            roomViewerCount.put(roomId, roomViewerCount.get(roomId) - 1);
        }

        // 更新房間內所有成員的在線人數
        broadcastViewerCount(roomId);
    }

    // 廣播當前房間的觀眾人數
    private void broadcastViewerCount(String roomId) {
        int viewerCount = roomViewerCount.get(roomId);
        JSONObject viewerCountMessage = new JSONObject()
                .put("type", "viewerCountUpdate")
                .put("viewerCount", viewerCount);

        log.info(viewerCountMessage.toString());

        // 發送給房間內所有觀眾和主播
        for (WebSocketSession viewer : rooms.get(roomId)) {
            sendToSession(viewer, viewerCountMessage.toString());
        }
        WebSocketSession broadcaster = broadcasters.get(roomId);
        if (broadcaster != null) {
            sendToSession(broadcaster, viewerCountMessage.toString());
        }
    }

    // 房間資訊內部類別
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RoomInfo {
        private String roomId;
        private String userImage;
        private String title;
        private String description;
        private Instant startTime;
        private int viewerCount;
        private List<String> products;

        public void incrementViewerCount() {
            viewerCount++;
        }

        public void decrementViewerCount() {
            viewerCount--;
        }

    }
}