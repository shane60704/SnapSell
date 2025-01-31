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

    @Override
    public List<ChatRoom> findChatRoomsByUserId(int userId) {
        String sql = "SELECT * FROM chat_room WHERE (user_a_id = :userId OR user_b_id = :userId)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        try {
            return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ChatRoom.class));
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

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

    @Override
    public Long createChatRoom(Long user1Id, Long user2Id, String uniqueChatroom) {
        String sql = "INSERT INTO chat_room (user_a_id, user_b_id, unique_chatroom, created_at) " +
                "VALUES (:user1Id, :user2Id, :uniqueChatroom, :created_at)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user1Id", user1Id);
        params.addValue("user2Id", user2Id);
        params.addValue("uniqueChatroom", uniqueChatroom);
        params.addValue("created_at", new Timestamp(System.currentTimeMillis()));

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        return keyHolder.getKey().longValue();
    }

    @Override
    public ChatRoom findChatRoomById(Long chatRoomId) {
        String sql = "SELECT * FROM chat_room WHERE id = :chatRoomId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chatRoomId", chatRoomId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(ChatRoom.class));
    }

    @Override
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