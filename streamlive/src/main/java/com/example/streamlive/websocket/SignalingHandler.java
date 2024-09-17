package com.example.streamlive.websocket;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class SignalingHandler extends TextWebSocketHandler {
    private Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private Map<String, WebSocketSession> broadcasters = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JSONObject jsonMessage = new JSONObject(message.getPayload());
        String type = jsonMessage.getString("type");
        String roomId = jsonMessage.getString("roomId");

        switch (type) {
            case "create":
                createRoom(roomId, session);
                break;
            case "join":
                joinRoom(roomId, session);
                break;
            case "offer":
            case "answer":
            case "candidate":
                forwardMessage(roomId, session, jsonMessage);
                break;
            case "chat":
                broadcastChat(roomId, session, jsonMessage.getString("content"));
                break;
        }
    }

    private void createRoom(String roomId, WebSocketSession session) {
        rooms.put(roomId, new CopyOnWriteArrayList<>());
        broadcasters.put(roomId, session);
        sendToSession(session, new JSONObject().put("type", "created").toString());
    }

    private void joinRoom(String roomId, WebSocketSession session) throws IOException {
        if (!rooms.containsKey(roomId)) {
            sendToSession(session, new JSONObject().put("type", "error").put("message", "Room not found").toString());
            return;
        }
        rooms.get(roomId).add(session);
        sendToSession(session, new JSONObject().put("type", "joined").toString());

        WebSocketSession broadcaster = broadcasters.get(roomId);
        sendToSession(broadcaster, new JSONObject().put("type", "newViewer").toString());
    }

    private void forwardMessage(String roomId, WebSocketSession sender, JSONObject message) throws IOException {
        WebSocketSession recipient = sender == broadcasters.get(roomId) ?
                rooms.get(roomId).get(rooms.get(roomId).size() - 1) : broadcasters.get(roomId);
        sendToSession(recipient, message.toString());
    }

    private void broadcastChat(String roomId, WebSocketSession sender, String content) throws IOException {
        JSONObject chatMessage = new JSONObject()
                .put("type", "chat")
                .put("content", content)
                .put("sender", sender == broadcasters.get(roomId) ? "Broadcaster" : "Viewer");

        for (WebSocketSession session : rooms.get(roomId)) {
            sendToSession(session, chatMessage.toString());
        }
        sendToSession(broadcasters.get(roomId), chatMessage.toString());
    }

    private void sendToSession(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        for (Map.Entry<String, WebSocketSession> entry : broadcasters.entrySet()) {
            if (entry.getValue() == session) {
                String roomId = entry.getKey();
                rooms.get(roomId).forEach(viewer -> {
                    try {
                        sendToSession(viewer, new JSONObject().put("type", "broadcasterLeft").toString());
                        viewer.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
                rooms.remove(roomId);
                broadcasters.remove(roomId);
                return;
            }
        }

        for (List<WebSocketSession> viewers : rooms.values()) {
            viewers.remove(session);
        }
    }
}

