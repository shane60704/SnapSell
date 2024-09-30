package com.example.streamlive.model.rowmapper;

import com.example.streamlive.model.livestream.LiveStatistics;
import com.example.streamlive.model.user.User;
import com.example.streamlive.model.user.UserInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(rs.getInt("id"));
        userInfo.setName(rs.getString("name"));
        userInfo.setEmail(rs.getString("email"));
        userInfo.setImage(rs.getString("image"));

        LiveStatistics liveStatistics = new LiveStatistics();
        liveStatistics.setTotalViewers(rs.getLong("totalViewers"));
        liveStatistics.setTotalQuantity(rs.getLong("totalQuantity"));
        liveStatistics.setTotalFigures(rs.getDouble("totalFigures"));
        liveStatistics.setAverageScore(rs.getDouble("averageScore"));

        return new User(userInfo, liveStatistics);
    }
}
