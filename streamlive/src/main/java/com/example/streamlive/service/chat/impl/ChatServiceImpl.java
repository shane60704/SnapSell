package com.example.streamlive.service.chat.impl;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.model.Message;
import com.example.streamlive.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatDao chatDao;

    // 保存訊息到資料庫的邏輯
    @Override
    public void saveMessage(Message message) {
        // 調用 DAO 來將訊息保存到資料庫
        chatDao.saveMessage(message);
    }
}
