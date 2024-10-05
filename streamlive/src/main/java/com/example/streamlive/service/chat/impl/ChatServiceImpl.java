package com.example.streamlive.service.chat.impl;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.chat.ChatMessage;
import com.example.streamlive.model.chat.RecentMessage;
import com.example.streamlive.model.chat.UserChatRoom;
import com.example.streamlive.model.user.UserInfo;
import com.example.streamlive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatDao chatDao;
    private final UserDao userDao;

    // 保存訊息到資料庫的邏輯
    @Override
    public void saveMessage(ChatMessage message) {
        // 調用 DAO 來將訊息保存到資料庫
        chatDao.saveMessage(message);
    }

    @Override
    public List<UserChatRoom> getUserChatRooms(int userId) {
        // 1.查找聊天室 id
        List<UserChatRoom> userChatRooms = new LinkedList<>();
        List<ChatRoom> chatRooms = chatDao.findChatRoomsByUserId(userId);
        for (int i = 0; i < chatRooms.size(); i++) {
            UserChatRoom userChatRoom = new UserChatRoom();
            int userAId = chatRooms.get(i).getUserAId();
            int userBId = chatRooms.get(i).getUserBId();
            UserInfo senderInfo = new UserInfo();
            UserInfo receiverInfo = new UserInfo();
            if(userBId != userId){
                 senderInfo = userDao.getUserInfoById(userAId);
                 receiverInfo = userDao.getUserInfoById(userBId);
            }else{
                 senderInfo = userDao.getUserInfoById(userBId);
                 receiverInfo = userDao.getUserInfoById(userAId);
            }
            RecentMessage recentMessage = chatDao.findRecentMessageByChatRoomId(chatRooms.get(i).getId());
            userChatRoom.setId(chatRooms.get(i).getId());
            userChatRoom.setUniqueChatroom(chatRooms.get(i).getUniqueChatroom());
            userChatRoom.setSenderInfo(senderInfo);
            userChatRoom.setReceiverInfo(receiverInfo);
            userChatRoom.setRecentMessage(recentMessage);
            userChatRooms.add(userChatRoom);
        }
        return userChatRooms;
    }

}
