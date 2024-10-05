package com.example.streamlive.model.chat;

import com.example.streamlive.model.Message;
import com.example.streamlive.model.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryMessages {
    private UserInfo client;
    private UserInfo agent;
    private List<Message> messages;
}
