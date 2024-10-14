package com.example.streamlive.websocket;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.dao.product.ProductDao;
import com.example.streamlive.dao.user.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalingHandler extends TextWebSocketHandler {

    private final LiveStreamDao liveStreamDao;
    private final ProductDao productDao;
    private final UserDao userDao;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;

    // Redis Key 定義
    private static final String ROOMS_KEY = "rooms"; // Hash key 用於存儲 RoomInfo
    private static final String BROADCASTERS_KEY = "broadcasters"; // Hash key 用於存儲廣播者
    private static final String ROOM_VIEWER_COUNT_KEY = "roomViewerCount"; // Hash key 用於存儲房間觀眾數量
    private static final String TOTAL_VIEWER_COUNT_KEY = "totalViewerCount"; // Hash key 用於存儲房間總觀眾數量
    private static final String LOBBY_SESSIONS_KEY = "lobbySessions"; // Set key 用於存儲大廳連線

    // 每個房間的觀眾連線會以 room:session:{roomId} 儲存在 Redis
    private String getRoomSessionsKey(String roomId) {
        return "room:session:" + roomId;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionManager.addSession(session.getId(), session);
        log.info("新的連線建立，sessionId: {}", session.getId());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("收到來自 sessionId {} 的訊息: {}", session.getId(), message.getPayload());
        JSONObject jsonMessage = new JSONObject(message.getPayload());
        String type = jsonMessage.getString("type");
        switch (type) {
            case "create":
                handleCreateRoom(jsonMessage, session);
                break;
            case "join":
                handleJoinRoom(jsonMessage, session);
                break;
            case "chat":
                handleChat(jsonMessage, session);
                break;
            case "getRoomList":
                sendRoomList(session);
                break;
            case "lobby":
                handleLobby(session);
                break;
            case "sendGift":
                handleSendGift(jsonMessage, session);
                break;
            default:
                log.warn("未知的訊息類型: {}", type);
        }
    }

    /**
     * 處理創建房間的邏輯
     */
    private void handleCreateRoom(JSONObject jsonMessage, WebSocketSession session) throws IOException {
        String createRoomId = jsonMessage.getString("roomId");
        String userId = jsonMessage.getString("userId");
        String title = jsonMessage.getString("title");
        String description = jsonMessage.getString("description");
        log.info("創建成功: {}", createRoomId);
        liveStreamDao.createLiveStreamRecord(userId, createRoomId);
        String userImage = userDao.getUserImageById(userId);
        JSONArray productsArray = jsonMessage.getJSONArray("products");
        List<String> productsList = new ArrayList<>();
        for (int i = 0; i < productsArray.length(); i++) {
            productsList.add(productsArray.getString(i));
        }
        createRoom(createRoomId, userImage, title, description, session, productsList);
    }

    /**
     * 創建房間並儲存至 Redis
     */
    private void createRoom(String roomId, String userImage, String title, String description, WebSocketSession session, List<String> products) {
        // 建立 RoomInfo 物件
        RoomInfo roomInfo = new RoomInfo(roomId, userImage, title, description, Instant.now(), 0, products);

        // 將 RoomInfo 儲存至 Redis 的 Hash 中
        redisTemplate.opsForHash().put(ROOMS_KEY, roomId, roomInfo);
        log.info("房間 {} 已儲存至 Redis: {}", roomId, roomInfo);

        // 設定廣播者
        redisTemplate.opsForHash().put(BROADCASTERS_KEY, roomId, session.getId());
        log.info("房間 {} 的廣播者 sessionId: {}", roomId, session.getId());

        // 建立房間的觀眾連線 Set，加入廣播者
        redisTemplate.opsForSet().add(getRoomSessionsKey(roomId), session.getId());
        log.info("房間 {} 的 sessionId 集合已添加廣播者: {}", roomId, session.getId());

        // 通知當前用戶房間已創建
        sendToSession(session, new JSONObject().put("type", "created").toString());
        log.info("已通知 sessionId {} 房間 {} 已創建", session.getId(), roomId);

        // 更新大廳的房間清單
        broadcastRoomList();
    }

    /**
     * 處理加入房間的邏輯
     */
    private void handleJoinRoom(JSONObject jsonMessage, WebSocketSession session) throws IOException {
        String joinRoomId = jsonMessage.getString("roomId");
        String joinUserId = jsonMessage.getString("userId");

        // 檢查房間是否存在
        RoomInfo roomInfo = (RoomInfo) redisTemplate.opsForHash().get(ROOMS_KEY, joinRoomId);
        if (roomInfo == null) {
            sendToSession(session, new JSONObject().put("type", "error").put("message", "房間不存在").toString());
            log.warn("房間 {} 不存在，無法加入", joinRoomId);
            return;
        }

        // 將觀眾加入房間的連線 Set
        redisTemplate.opsForSet().add(getRoomSessionsKey(joinRoomId), session.getId());
        log.info("sessionId {} 已加入房間 {}", session.getId(), joinRoomId);

        // 增加房間的觀眾數量
        incrementViewerCount(joinRoomId);

        // 通知主播和其他觀眾更新觀眾數量
        broadcastViewerCount(joinRoomId);

        // 發送 'joined' 訊息，包含 viewerId
        sendToSession(session, new JSONObject().put("type", "joined").put("viewerId", joinUserId).toString());
        log.info("已通知 sessionId {} 加入房間 {}", session.getId(), joinRoomId);

        // 通知主播有新的觀眾加入
        String broadcasterSessionId = (String) redisTemplate.opsForHash().get(BROADCASTERS_KEY, joinRoomId);
        WebSocketSession broadcasterSession = sessionManager.getSession(broadcasterSessionId);
        if (broadcasterSession != null && broadcasterSession.isOpen()) {
            sendToSession(broadcasterSession, new JSONObject().put("type", "newViewer").put("viewerId", joinUserId).toString());
            log.info("已通知主播 sessionId {} 新的觀眾加入: {}", broadcasterSessionId, joinUserId);
        } else {
            log.warn("無法找到或廣播者 sessionId: {}，無法通知新觀眾加入", broadcasterSessionId);
        }

        // 更新大廳的房間清單
        broadcastRoomList();
    }

    /**
     * 處理聊天訊息
     */
    private void handleChat(JSONObject jsonMessage, WebSocketSession sender) throws IOException {
        String chatRoomId = jsonMessage.getString("roomId");
        String content = jsonMessage.getString("content");
        log.info("收到來自 sessionId {} 的聊天訊息: {}", sender.getId(), content);
        broadcastChat(chatRoomId, sender, content);
    }

    /**
     * 處理大廳連線的邏輯
     */
    private void handleLobby(WebSocketSession session) {
        // 將連線加入大廳的 Set 中
        redisTemplate.opsForSet().add(LOBBY_SESSIONS_KEY, session.getId());
        log.info("sessionId {} 已加入大廳連線", session.getId());

        // 發送房間清單
        sendRoomList(session);
    }

    /**
     * 處理送禮物的邏輯
     */
    private void handleSendGift(JSONObject jsonMessage, WebSocketSession sender) throws IOException {
        String giftRoomId = jsonMessage.getString("roomId");
        String gift = jsonMessage.getString("gift");
        String senderName = jsonMessage.getString("sender");
        log.info("sessionId {} 送出禮物 {} 至房間 {}", sender.getId(), gift, giftRoomId);
        broadcastGift(giftRoomId, senderName, gift);
    }

    /**
     * 廣播聊天室訊息給所有房間內的成員
     */
    private void broadcastChat(String roomId, WebSocketSession sender, String content) throws IOException {
        JSONObject chatMessage = new JSONObject()
                .put("type", "chat")
                .put("content", content)
                .put("sender", isBroadcaster(roomId, sender) ? "主播" : "觀眾");

        log.info("正在廣播聊天訊息到房間 {}: {}", roomId, chatMessage.toString());

        // 取得房間內所有觀眾的 sessionId，包括廣播者
        Set<Object> sessionIds = redisTemplate.opsForSet().members(getRoomSessionsKey(roomId));
        if (sessionIds != null) {
            for (Object sessionIdObj : sessionIds) {
                String sessionId = (String) sessionIdObj;
                WebSocketSession session = sessionManager.getSession(sessionId);
                if (session != null && session.isOpen()) {
                    sendToSession(session, chatMessage.toString());
                }
            }
        }

        log.info("聊天訊息已廣播至房間 {}", roomId);
    }

    /**
     * 發送房間清單給指定的 session
     */
    private void sendRoomList(WebSocketSession session) {
        log.info("正在發送房間清單給 sessionId: {}", session.getId());

        List<Object> rawRoomList = redisTemplate.opsForHash().values(ROOMS_KEY);
        log.info("從 Redis 獲取的原始房間資料: {}", rawRoomList);

        List<RoomInfo> roomList = new ArrayList<>();
        for (Object obj : rawRoomList) {
            try {
                // 直接將物件轉換為 RoomInfo
                RoomInfo roomInfo = objectMapper.convertValue(obj, RoomInfo.class);
                roomList.add(roomInfo);
                log.info("成功轉換 RoomInfo: {}", roomInfo);
            } catch (Exception e) {
                log.error("轉換 RoomInfo 失敗: {}", e.getMessage());
            }
        }

        if (roomList.isEmpty()) {
            log.warn("房間清單為空！");
        } else {
            log.info("獲取到 {} 個房間資訊", roomList.size());
        }

        List<JSONObject> roomJsonList = new ArrayList<>();
        for (RoomInfo roomInfo : roomList) {
            JSONObject roomJson = new JSONObject()
                    .put("roomId", roomInfo.getRoomId())
                    .put("title", roomInfo.getTitle())
                    .put("userImage", roomInfo.getUserImage())
                    .put("description", roomInfo.getDescription())
                    .put("startTime", roomInfo.getStartTime().toString())
                    .put("viewerCount", roomInfo.getViewerCount())
                    .put("products", new JSONArray(roomInfo.getProducts()));
            roomJsonList.add(roomJson);
        }

        JSONObject message = new JSONObject()
                .put("type", "roomList")
                .put("rooms", roomJsonList);
        sendToSession(session, message.toString());
        log.info("房間清單已發送給 sessionId: {}", session.getId());
    }

    /**
     * 廣播房間清單給所有大廳的連線
     */
    private void broadcastRoomList() {
        log.info("正在廣播房間清單給所有大廳連線");
        Set<Object> lobbySessionIds = redisTemplate.opsForSet().members(LOBBY_SESSIONS_KEY);
        if (lobbySessionIds == null || lobbySessionIds.isEmpty()) {
            log.warn("大廳連線為空，無需廣播房間清單");
            return;
        }

        for (Object sessionIdObj : lobbySessionIds) {
            String sessionId = (String) sessionIdObj;
            WebSocketSession session = sessionManager.getSession(sessionId);
            if (session != null && session.isOpen()) {
                log.info("發送房間清單給 sessionId: {}", sessionId);
                sendRoomList(session);
            } else {
                log.warn("無法找到或關閉的 sessionId: {}", sessionId);
            }
        }
        log.info("房間清單已廣播完畢");
    }

    /**
     * 當觀眾加入房間時呼叫此方法，增加累積觀眾數量
     */
    private void incrementViewerCount(String roomId) {
        // 增加總觀眾數量
        redisTemplate.opsForHash().increment(TOTAL_VIEWER_COUNT_KEY, roomId, 1);

        // 增加當前房間的觀眾數量
        redisTemplate.opsForHash().increment(ROOM_VIEWER_COUNT_KEY, roomId, 1);

        Object totalViewerCountObj = redisTemplate.opsForHash().get(TOTAL_VIEWER_COUNT_KEY, roomId);
        Object currentViewerCountObj = redisTemplate.opsForHash().get(ROOM_VIEWER_COUNT_KEY, roomId);

        log.info("房間 {} 的累積觀眾人數: {}", roomId, totalViewerCountObj);
        log.info("房間 {} 的當前觀眾人數: {}", roomId, currentViewerCountObj);
    }

    /**
     * 當觀眾離開房間時呼叫此方法，減少累積觀眾數量
     */
    private void decrementViewerCount(String roomId) {
        // 減少當前房間的觀眾數量
        redisTemplate.opsForHash().increment(ROOM_VIEWER_COUNT_KEY, roomId, -1);

        Object currentViewerCountObj = redisTemplate.opsForHash().get(ROOM_VIEWER_COUNT_KEY, roomId);
        log.info("房間 {} 的當前觀眾人數: {}", roomId, currentViewerCountObj);
    }

    /**
     * 當觀眾離開房間時呼叫此方法，更新觀眾數量並通知所有成員
     */
    private void onViewerLeft(String roomId) {
        decrementViewerCount(roomId);
        broadcastViewerCount(roomId);
    }

    /**
     * 廣播當前房間的觀眾數量給所有成員
     */
    private void broadcastViewerCount(String roomId) {
        Object viewerCountObj = redisTemplate.opsForHash().get(ROOM_VIEWER_COUNT_KEY, roomId);
        int viewerCount = viewerCountObj != null ? ((Number) viewerCountObj).intValue() : 0;

        JSONObject viewerCountMessage = new JSONObject()
                .put("type", "viewerCountUpdate")
                .put("viewerCount", viewerCount);

        log.info("正在廣播房間 {} 的觀眾數量: {}", roomId, viewerCount);

        // 取得房間內所有觀眾的 sessionId
        Set<Object> sessionIds = redisTemplate.opsForSet().members(getRoomSessionsKey(roomId));
        if (sessionIds != null) {
            for (Object sessionIdObj : sessionIds) {
                String sessionId = (String) sessionIdObj;
                WebSocketSession session = sessionManager.getSession(sessionId);
                if (session != null && session.isOpen()) {
                    sendToSession(session, viewerCountMessage.toString());
                }
            }
        }

        // 發送給主播
        String broadcasterSessionId = (String) redisTemplate.opsForHash().get(BROADCASTERS_KEY, roomId);
        WebSocketSession broadcasterSession = sessionManager.getSession(broadcasterSessionId);
        if (broadcasterSession != null && broadcasterSession.isOpen()) {
            sendToSession(broadcasterSession, viewerCountMessage.toString());
        }

        log.info("觀眾數量已廣播至房間 {}", roomId);
    }

    /**
     * 廣播禮物訊息給所有房間內的成員
     */
    private void broadcastGift(String roomId, String sender, String gift) throws IOException {
        JSONObject giftMessage = new JSONObject()
                .put("type", "gift")
                .put("sender", sender)
                .put("gift", gift);

        log.info("正在廣播禮物訊息到房間 {}: {}", roomId, giftMessage.toString());

        // 取得房間內所有觀眾的 sessionId，包括廣播者
        Set<Object> sessionIds = redisTemplate.opsForSet().members(getRoomSessionsKey(roomId));
        if (sessionIds != null) {
            for (Object sessionIdObj : sessionIds) {
                String sessionId = (String) sessionIdObj;
                WebSocketSession session = sessionManager.getSession(sessionId);
                if (session != null && session.isOpen()) {
                    sendToSession(session, giftMessage.toString());
                }
            }
        }

        log.info("禮物訊息已廣播至房間 {}", roomId);
    }

    /**
     * 發送訊息給指定的 session
     */
    private void sendToSession(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
                log.info("已發送訊息給 sessionId {}: {}", session.getId(), message);
            }
        } catch (IOException e) {
            // 處理發送訊息時的異常
            log.error("發送訊息時發生錯誤: {}", e.getMessage());
        }
    }

    /**
     * 當連線關閉時處理相應邏輯
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("連線已關閉，sessionId: {}, 狀態: {}", session.getId(), status);
        // 移除大廳連線
        redisTemplate.opsForSet().remove(LOBBY_SESSIONS_KEY, session.getId());
        log.info("已移除 sessionId {} 從大廳連線", session.getId());
        // 移除 session 映射
        sessionManager.removeSession(session.getId());
        log.info("已移除 sessionId {} 從 SessionManager", session.getId());

        String roomIdToRemove = null;

        // 檢查是否是廣播者離線
        Map<Object, Object> broadcasters = redisTemplate.opsForHash().entries(BROADCASTERS_KEY);
        for (Map.Entry<Object, Object> entry : broadcasters.entrySet()) {
            if (entry.getValue().equals(session.getId())) {
                String roomId = (String) entry.getKey();
                roomIdToRemove = roomId;

                // 取得直播統計資料
                Map<String, Object> liveStatistics = liveStreamDao.getTotalPriceAndQuantity(roomId);
                BigDecimal totalPriceDecimal = (BigDecimal) liveStatistics.get("totalPrice");
                BigDecimal totalQuantityDecimal = (BigDecimal) liveStatistics.get("totalQuantity");

                int totalPrice = totalPriceDecimal != null ? totalPriceDecimal.intValue() : 0;
                int totalQuantity = totalQuantityDecimal != null ? totalQuantityDecimal.intValue() : 0;

                // 取得總觀眾數量
                Object viewerCountObj = redisTemplate.opsForHash().get(TOTAL_VIEWER_COUNT_KEY, roomId);
                int viewerCount = viewerCountObj != null ? ((Number) viewerCountObj).intValue() : 0;

                log.info("更新直播記錄，房間 {}: 觀眾數量={}, 購買數量={}, 總金額={}", roomId, viewerCount, totalQuantity, totalPrice);
                liveStreamDao.updateLiveStreamRecord(roomId, viewerCount, totalQuantity, totalPrice);

                // 取得房間內所有觀眾的 sessionId
                Set<Object> sessionIds = redisTemplate.opsForSet().members(getRoomSessionsKey(roomId));
                if (sessionIds != null) {
                    for (Object sessionIdObj : sessionIds) {
                        String viewerSessionId = (String) sessionIdObj;
                        WebSocketSession viewerSession = sessionManager.getSession(viewerSessionId);
                        if (viewerSession != null && viewerSession.isOpen()) {
                            try {
                                sendToSession(viewerSession, new JSONObject().put("type", "broadcasterLeft").toString());
                                viewerSession.close();
                                log.info("已通知並關閉觀眾 sessionId {} 連線", viewerSessionId);
                            } catch (IOException e) {
                                log.error("關閉觀眾連線時發生錯誤: {}", e.getMessage());
                            }
                        }
                    }
                }

                // 移除房間相關的資料
                redisTemplate.opsForHash().delete(ROOMS_KEY, roomId);
                redisTemplate.opsForHash().delete(BROADCASTERS_KEY, roomId);
                redisTemplate.delete(getRoomSessionsKey(roomId));
                redisTemplate.opsForHash().delete(ROOM_VIEWER_COUNT_KEY, roomId);
                redisTemplate.opsForHash().delete(TOTAL_VIEWER_COUNT_KEY, roomId);
                log.info("已移除房間 {} 的所有相關資料", roomId);

                break;
            }
        }

        if (roomIdToRemove != null) {
            // 更新大廳的房間清單
            broadcastRoomList();
            log.info("房間 {} 已移除，並更新大廳房間清單", roomIdToRemove);
            return;
        }

        // 檢查是否是觀眾離線
        Map<Object, Object> allRooms = redisTemplate.opsForHash().entries(ROOMS_KEY);
        for (Map.Entry<Object, Object> entry : allRooms.entrySet()) {
            String roomId = (String) entry.getKey();
            Set<Object> sessionIds = redisTemplate.opsForSet().members(getRoomSessionsKey(roomId));
            if (sessionIds != null && sessionIds.contains(session.getId())) {
                // 移除觀眾的連線
                redisTemplate.opsForSet().remove(getRoomSessionsKey(roomId), session.getId());
                log.info("已移除 sessionId {} 從房間 {}", session.getId(), roomId);

                // 減少觀眾數量
                onViewerLeft(roomId);

                // 通知主播有觀眾離開
                String broadcasterSessionId = (String) redisTemplate.opsForHash().get(BROADCASTERS_KEY, roomId);
                WebSocketSession broadcasterSession = sessionManager.getSession(broadcasterSessionId);
                if (broadcasterSession != null && broadcasterSession.isOpen()) {
                    JSONObject message = new JSONObject()
                            .put("type", "viewerLeft")
                            .put("viewerId", session.getId());
                    sendToSession(broadcasterSession, message.toString());
                    log.info("已通知主播 sessionId {} 觀眾 {} 離開房間 {}", broadcasterSessionId, session.getId(), roomId);
                }

                // 更新大廳的房間清單
                broadcastRoomList();
                break;
            }
        }

        super.afterConnectionClosed(session, status);
    }

    /**
     * 判斷指定的 session 是否為廣播者
     */
    private boolean isBroadcaster(String roomId, WebSocketSession session) {
        String broadcasterSessionId = (String) redisTemplate.opsForHash().get(BROADCASTERS_KEY, roomId);
        return broadcasterSessionId != null && broadcasterSessionId.equals(session.getId());
    }

    public void notifyBroadcaster(String roomId, JSONObject message) {

        String broadcasterSessionId = (String) redisTemplate.opsForHash().get(BROADCASTERS_KEY, roomId);

        WebSocketSession broadcasterSession = sessionManager.getSession(broadcasterSessionId);
        if (broadcasterSession != null && broadcasterSession.isOpen()) {
            sendToSession(broadcasterSession, message.toString());
            log.info("已通知直播主 sessionId {} 商品已售出: {}", broadcasterSessionId, message);
        } else {
            log.warn("無法找到或廣播者 sessionId: {}，無法通知商品售出", broadcasterSessionId);
        }
    }

    public void broadcastToViewers(String roomId, JSONObject message) {

        Set<Object> sessionIds = redisTemplate.opsForSet().members(getRoomSessionsKey(roomId));
        if (sessionIds != null) {
            for (Object sessionIdObj : sessionIds) {
                String sessionId = (String) sessionIdObj;
                WebSocketSession session = sessionManager.getSession(sessionId);
                if (session != null && session.isOpen()) {
                    sendToSession(session, message.toString());
                }
            }
        }
        log.info("已廣播商品更新消息至房間 {}", roomId);
    }

    /**
     * 房間資訊的內部類別
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomInfo implements Serializable {
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