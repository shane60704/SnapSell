package com.example.streamlive.service.chat;

import com.example.streamlive.model.Message;
import com.example.streamlive.model.chat.UserChatRoom;

import java.util.List;

public interface ChatService {
    void saveMessage(Message message);
    List<UserChatRoom> getUserChatRooms(int userId);
}
