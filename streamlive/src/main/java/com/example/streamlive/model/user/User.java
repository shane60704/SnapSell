package com.example.streamlive.model.user;

import com.example.streamlive.model.livestream.LiveStatistics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UserInfo userInfo;
    private LiveStatistics liveStatistics;
}
