package com.example.streamlive.dao.chat;

import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.Message;

import java.util.List;

public interface ChatDao {
    List<ChatRoom> findChatRoomsByUserId(int userId);
    ChatRoom findChatRoomByUsersId(Long user1Id, Long user2Id);
    Long createChatRoom(Long user1Id, Long user2Id, String uniqueChatroom);
    ChatRoom findChatRoomById(Long chatRoomId);
    void saveMessage(Message message);
    List<Message> findMessagesByChatRoomId(Long chatRoomId, String start, String end);
}
