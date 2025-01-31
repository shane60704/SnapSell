package com.example.streamlive.service.chat;

import com.example.streamlive.model.chat.ChatMessage;
import com.example.streamlive.model.chat.HistoryMessages;
import com.example.streamlive.model.chat.UserChatRoom;

import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessage message);
    List<UserChatRoom> getUserChatRooms(int userId);
    HistoryMessages getChatHistory (Long chatRoomId,String start,String end);
}
