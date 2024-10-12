package com.example.streamlive.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Session 管理器，用於將 sessionId 與 WebSocketSession 進行映射
 */
@Component
public class SessionManager {

    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 添加 session
     *
     * @param sessionId   連線的 sessionId
     * @param webSocketSession WebSocketSession 物件
     */
    public static void addSession(String sessionId, WebSocketSession webSocketSession) {
        sessions.put(sessionId, webSocketSession);
    }

    /**
     * 移除 session
     *
     * @param sessionId 連線的 sessionId
     */
    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * 根據 sessionId 獲取 WebSocketSession
     *
     * @param sessionId 連線的 sessionId
     * @return 對應的 WebSocketSession，若不存在則返回 null
     */
    public static WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
}