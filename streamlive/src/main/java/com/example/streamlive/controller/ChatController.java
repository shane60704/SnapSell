package com.example.streamlive.controller;

import com.example.streamlive.model.chat.ChatMessage;
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
    public void sendMessage(ChatMessage chatMessage) {

        chatService.saveMessage(chatMessage);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChatRoomId(), chatMessage);
    }

}

