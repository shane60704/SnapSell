package com.example.streamlive.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    // 訊息唯一 ID
    private int id;

    // 聊天室 ID
    private int chatRoomId;

    // 發送者 ID
    private int senderId;

    // 訊息內容
    private String content;

    // 訊息發送時間
    private String timestamp;

    private String imgSrc;

}
