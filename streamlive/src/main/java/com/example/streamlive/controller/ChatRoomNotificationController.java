package com.example.streamlive.controller;

import com.example.streamlive.dao.chat.Impl.ChatDaoImpl;
import com.example.streamlive.model.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/1.0")
public class ChatRoomNotificationController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatDaoImpl chatDao;

    // 創建聊天室，並通知 B
    @PostMapping("/chatroom")
    public ChatRoom createOrGetChatRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        ChatRoom chatRoom = chatDao.findChatRoomByUsersId(user1Id, user2Id);
        log.info("HaveRoom");
        if (chatRoom == null) {
            String uniqueChatroom = "room_" + user1Id + "_" + user2Id; // 唯一標識符
            Long chatRoomId = chatDao.createChatRoom(user1Id, user2Id, uniqueChatroom);
            chatRoom = chatDao.findChatRoomById(chatRoomId);
            log.info("CreateRoom");
            // 通知 B 有新聊天室
            simpMessagingTemplate.convertAndSend("/topic/newRoom/" + user2Id, chatRoom);
        }
        return chatRoom;
    }
}
