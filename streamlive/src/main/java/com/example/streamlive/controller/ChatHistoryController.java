package com.example.streamlive.controller;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.dto.response.APIResponse;
import com.example.streamlive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/")
public class ChatHistoryController {
    private final ChatDao chatDao;
    private final UserDao userDao;
    private final ChatService chatService;

    @GetMapping("/users/{userId}/chatrooms")
    public ResponseEntity<?> getUserChatRooms(@PathVariable int userId) {
        return ResponseEntity.ok(new APIResponse<>(chatService.getUserChatRooms(userId)));
    }

    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        return ResponseEntity.ok(chatService.getChatHistory(chatRoomId, start, end));
    }
}
