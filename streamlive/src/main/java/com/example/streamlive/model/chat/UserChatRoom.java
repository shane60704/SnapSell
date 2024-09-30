package com.example.streamlive.model.chat;

import com.example.streamlive.model.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChatRoom {
    private int id;
    private String uniqueChatroom;
    private UserInfo senderInfo;
    private UserInfo receiverInfo;
    private RecentMessage recentMessage;
}
