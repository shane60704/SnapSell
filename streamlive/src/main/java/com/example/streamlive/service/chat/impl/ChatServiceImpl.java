package com.example.streamlive.service.chat.impl;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.Message;
import com.example.streamlive.model.chat.ChatMessage;
import com.example.streamlive.model.chat.HistoryMessages;
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

    @Override
    public void saveMessage(ChatMessage message) {
        chatDao.saveMessage(message);
    }

    @Override
    public List<UserChatRoom> getUserChatRooms(int userId) {
        List<ChatRoom> chatRooms = chatDao.findChatRoomsByUserId(userId);
        List<UserChatRoom> userChatRooms = new LinkedList<>();

        for (ChatRoom chatRoom : chatRooms) {
            UserChatRoom userChatRoom = createUserChatRoom(chatRoom, userId);
            userChatRooms.add(userChatRoom);
        }

        return userChatRooms;
    }

    @Override
    public HistoryMessages getChatHistory (Long chatRoomId, String start, String end){
        ChatRoom chatRoom = chatDao.findChatRoomById(chatRoomId);
        UserInfo client = userDao.getUserInfoById(chatRoom.getUserAId());
        UserInfo agent = userDao.getUserInfoById(chatRoom.getUserBId());
        List<Message> messages = chatDao.findMessagesByChatRoomId(chatRoomId, start, end);
        HistoryMessages historyMessages = new HistoryMessages(client, agent, messages);
        return historyMessages;
    }

    private UserChatRoom createUserChatRoom(ChatRoom chatRoom, int userId) {
        UserChatRoom userChatRoom = new UserChatRoom();

        int userAId = chatRoom.getUserAId();
        int userBId = chatRoom.getUserBId();

        UserInfo senderInfo = getUserInfo(userAId, userBId, userId);
        UserInfo receiverInfo = getUserInfo(userBId, userAId, userId);

        RecentMessage recentMessage = chatDao.findRecentMessageByChatRoomId(chatRoom.getId());

        userChatRoom.setId(chatRoom.getId());
        userChatRoom.setUniqueChatroom(chatRoom.getUniqueChatroom());
        userChatRoom.setSenderInfo(senderInfo);
        userChatRoom.setReceiverInfo(receiverInfo);
        userChatRoom.setRecentMessage(recentMessage);

        return userChatRoom;
    }

    private UserInfo getUserInfo(int userIdToFetch, int otherUserId, int currentUserId) {
        if (userIdToFetch != currentUserId) {
            return userDao.getUserInfoById(userIdToFetch);
        }
        return userDao.getUserInfoById(otherUserId);
    }

}
