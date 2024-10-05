package com.example.streamlive.controller;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.dto.response.ApiResponse;
import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.Message;
import com.example.streamlive.model.chat.HistoryMessages;
import com.example.streamlive.model.user.UserInfo;
import com.example.streamlive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/")
public class ChatHistoryController {
    private final ChatDao chatDao;
    private final UserDao userDao;
    private final ChatService chatService;

    // 查詢用戶參與的所有聊天室
    @GetMapping("/users/{userId}/chatrooms")
    public ResponseEntity<?> getUserChatRooms(@PathVariable int userId) {
        return ResponseEntity.ok(new ApiResponse<>(chatService.getUserChatRooms(userId)));
    }

    // 查詢特定聊天室的歷史聊天記錄
    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        ChatRoom chatRoom = chatDao.findChatRoomById(chatRoomId);
        UserInfo client = userDao.getUserInfoById(chatRoom.getUserAId());
        UserInfo agent = userDao.getUserInfoById(chatRoom.getUserBId());
        List<Message> messages = chatDao.findMessagesByChatRoomId(chatRoomId, start, end);
        HistoryMessages historyMessages = new HistoryMessages(client, agent, messages);
        return ResponseEntity.ok(historyMessages);
    }
}
