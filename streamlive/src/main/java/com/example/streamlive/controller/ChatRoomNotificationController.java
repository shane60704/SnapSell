package com.example.streamlive.controller;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.chat.UserChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/1.0")
public class ChatRoomNotificationController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatDao chatDao;
    private final UserDao userDao;

    // 創建聊天室，並通知 B
    @PostMapping("/chatroom")
    public ChatRoom createOrGetChatRoom(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        ChatRoom chatRoom = chatDao.findChatRoomByUsersId(user1Id, user2Id);
        if (chatRoom == null) {
            log.info("NoRoom");
            String uniqueChatroom = "room_" + user1Id + "_" + user2Id; // 唯一標識符
            Long chatRoomId = chatDao.createChatRoom(user1Id, user2Id, uniqueChatroom);
            chatRoom = chatDao.findChatRoomById(chatRoomId);
            log.info("CreateRoom");
            // 通知 B 有新聊天室
            simpMessagingTemplate.convertAndSend("/topic/newRoom/" + user2Id, chatRoom);
        }
        return chatRoom;
    }

    @GetMapping("/chatroom/info")
    public UserChatRoom createChatRoom(@RequestParam Long currentUserId, @RequestParam Long otherUserId) {
        UserChatRoom userChatRoom = new UserChatRoom();
        ChatRoom chatRoom = chatDao.findChatRoomByUsersId(currentUserId, otherUserId);
        userChatRoom.setId(chatRoom.getId());
        userChatRoom.setUniqueChatroom(chatRoom.getUniqueChatroom());
        userChatRoom.setSenderInfo(userDao.getUserInfoByUserId(currentUserId));
        userChatRoom.setReceiverInfo(userDao.getUserInfoByUserId(otherUserId));
        userChatRoom.setRecentMessage(chatDao.findRecentMessageByChatRoomId(chatRoom.getId()));
        return userChatRoom;
    }

}
