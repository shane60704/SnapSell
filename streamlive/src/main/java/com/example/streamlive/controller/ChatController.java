package com.example.streamlive.controller;

import com.example.streamlive.model.Message;
import com.example.streamlive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(Message chatMessage) {
        // 保存訊息到資料庫
        chatService.saveMessage(chatMessage);

        // 將訊息廣播到聊天室的所有訂閱者
        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChatRoomId(), chatMessage);
    }
}

