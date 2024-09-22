package com.example.streamlive.controller;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/")
public class ChatHistoryController {
    private final ChatDao chatDao;

    // 查詢用戶參與的所有聊天室
    @GetMapping("/users/{userId}/chatrooms")
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(@PathVariable int userId) {
        List<ChatRoom> chatRooms = chatDao.findChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    // 查詢特定聊天室的歷史聊天記錄
    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<List<Message>> getChatHistory(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        List<Message> messages = chatDao.findMessagesByChatRoomId(chatRoomId, start, end);
        return ResponseEntity.ok(messages);
    }
}
