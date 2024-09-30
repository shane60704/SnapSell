package com.example.streamlive.dao.chat;

import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.Message;
import com.example.streamlive.model.chat.RecentMessage;

import java.util.List;

public interface ChatDao {
    List<ChatRoom> findChatRoomsByUserId(int userId);
//    ChatRoom findChatRoomByRoomId(int roomId);
    ChatRoom findChatRoomByUsersId(Long user1Id, Long user2Id);
    Long createChatRoom(Long user1Id, Long user2Id, String uniqueChatroom);
    ChatRoom findChatRoomById(Long chatRoomId);
    void saveMessage(Message message);
    List<Message> findMessagesByChatRoomId(Long chatRoomId, String start, String end);
    RecentMessage findRecentMessageByChatRoomId(int chatRoomId);
}
