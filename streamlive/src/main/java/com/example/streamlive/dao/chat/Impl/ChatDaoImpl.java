package com.example.streamlive.dao.chat.Impl;

import com.example.streamlive.dao.chat.ChatDao;
import com.example.streamlive.model.ChatRoom;
import com.example.streamlive.model.Message;
import com.example.streamlive.model.chat.ChatMessage;
import com.example.streamlive.model.chat.RecentMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatDaoImpl implements ChatDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    // 根據用戶 ID 查找其所屬聊天室
    @Override
    public List<ChatRoom> findChatRoomsByUserId(int userId) {
        String sql = "SELECT * FROM chat_room WHERE (user_a_id = :userId OR user_b_id = :userId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        // 查詢結果映射到 ChatRoom 對象
        try {
            return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ChatRoom.class));
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 根據兩個用戶的ID查詢聊天室
    @Override
    public ChatRoom findChatRoomByUsersId(Long user1Id, Long user2Id) {
        String sql = "SELECT * FROM chat_room WHERE (user_a_id = :user1Id AND user_b_id = :user2Id) "
                + "OR (user_a_id = :user2Id AND user_b_id = :user1Id)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user1Id", user1Id);
        params.addValue("user2Id", user2Id);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql,params,new BeanPropertyRowMapper<>(ChatRoom.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 建立聊天室
    @Override
    public Long createChatRoom(Long user1Id, Long user2Id, String uniqueChatroom) {
        // 定義 SQL 插入語句，插入 user_a_id, user_b_id 和 unique_chatroom
        String sql = "INSERT INTO chat_room (user_a_id, user_b_id, unique_chatroom, created_at) " +
                "VALUES (:user1Id, :user2Id, :uniqueChatroom, :created_at)";

        // 建立參數映射
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user1Id", user1Id);
        params.addValue("user2Id", user2Id);
        params.addValue("uniqueChatroom", uniqueChatroom);
        params.addValue("created_at", new Timestamp(System.currentTimeMillis()));

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
    public void saveMessage(ChatMessage message) {
        String sql = "INSERT INTO messages (chat_room_id, sender_id, content, timestamp) " +
                "VALUES (:chatRoomId, :senderId, :content, :timestamp)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chatRoomId", message.getChatRoomId());
        params.addValue("senderId", message.getSenderId());
        params.addValue("content", message.getContent());
        params.addValue("timestamp", new Timestamp(System.currentTimeMillis()));

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

    @Override
    public RecentMessage findRecentMessageByChatRoomId(int chatRoomId) {
        String sql = "SELECT content,timestamp FROM messages WHERE chat_room_id = :chatRoomId ORDER BY timestamp DESC LIMIT 1";
        Map<String, Object> map = new HashMap<>();
        map.put("chatRoomId", chatRoomId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(RecentMessage.class));
        } catch (DataAccessException e) {
            return null;
        }
    }

}