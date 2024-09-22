package com.example.streamlive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    // 聊天室唯一 ID
    private int id;

    // 第一位用戶 ID（user_a_id）
    private int userAId;

    // 第二位用戶 ID（user_b_id）
    private int userBId;

    // 聊天室唯一標識符或名稱
    private String uniqueChatroom;

    // 聊天室創建時間
    private String createdAt;
}
