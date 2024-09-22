package com.example.streamlive.dao.chat.Impl;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatDaoImpl implements ChatDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    // 根據用戶 ID 查找其所屬聊天室
    @Override
    public List<ChatRoom> findChatRoomsByUserId(int userId) {
        String sql = "SELECT * FROM chat_room WHERE (user_a_id = :userId OR user_b_id = :userId)";

        // 使用 NamedParameterJdbcTemplate 查找聊天室
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        // 查詢結果映射到 ChatRoom 對象
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ChatRoom.class));
    }

    // 根據兩個用戶的ID查詢聊天室
    @Override
    public ChatRoom findChatRoomByUsersId(Long user1Id, Long user2Id) {
        // SQL 查詢，無論 user1 和 user2 是 user_a_id 還是 user_b_id 都能匹配
        String sql = "SELECT * FROM chat_room WHERE (user_a_id = :user1Id AND user_b_id = :user2Id) "
                + "OR (user_a_id = :user2Id AND user_b_id = :user1Id)";
        // 創建參數映射
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user1Id", user1Id);
        params.addValue("user2Id", user2Id);
        // 查詢並返回結果
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    sql,
                    params,
                    new BeanPropertyRowMapper<>(ChatRoom.class)
            );
        } catch (EmptyResultDataAccessException e) {
            // 如果沒有找到結果，返回 null
            return null;
        }
    }

    // 建立聊天室
    @Override
    public Long createChatRoom(Long user1Id, Long user2Id, String uniqueChatroom) {
        // 定義 SQL 插入語句，插入 user_a_id, user_b_id 和 unique_chatroom
        String sql = "INSERT INTO chat_room (user_a_id, user_b_id, unique_chatroom, created_at) " +
                "VALUES (:user1Id, :user2Id, :uniqueChatroom, CURRENT_TIMESTAMP)";

        // 建立參數映射
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user1Id", user1Id);
        params.addValue("user2Id", user2Id);
        params.addValue("uniqueChatroom", uniqueChatroom);

        // 使用 KeyHolder 來獲取自動生成的聊天室 ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 執行插入操作，返回自動生成的 ID
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        // 返回自動生成的聊天室 ID
        return keyHolder.getKey().longValue();
    }

    // 根據聊天室 ID 查找聊天室
    @Override
    public ChatRoom findChatRoomById(Long chatRoomId) {
        String sql = "SELECT * FROM chat_room WHERE id = :chatRoomId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chatRoomId", chatRoomId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(ChatRoom.class));
    }

    @Override
    // 保存聊天紀錄
    public void saveMessage(Message message) {
        String sql = "INSERT INTO messages (chat_room_id, sender_id, content, timestamp) " +
                "VALUES (:chatRoomId, :senderId, :content, :timestamp)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chatRoomId", message.getChatRoomId());
        params.addValue("senderId", message.getSenderId());
        params.addValue("content", message.getContent());
        params.addValue("timestamp", LocalDateTime.now());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);
    }

    @Override
    public List<Message> findMessagesByChatRoomId(Long chatRoomId, String start, String end) {
        String sql = "SELECT * FROM messages WHERE chat_room_id = :chatRoomId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chatRoomId", chatRoomId);

        if (start != null && end != null) {
            sql += " AND timestamp BETWEEN :start AND :end";
            params.addValue("start", start);
            params.addValue("end", end);
        }

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Message.class));
    }
}
