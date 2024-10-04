package com.example.streamlive.dao.livestream.Impl;

import com.example.streamlive.dao.livestream.LiveStreamDao;
import com.example.streamlive.dto.SatisfactionDto;
import com.example.streamlive.model.livestream.LiveStreamRecord;
import com.example.streamlive.model.livestream.LiveSummary;
import com.example.streamlive.model.livestream.Satisfaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
public class LiveStreamDaoImpl implements LiveStreamDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Long createLiveStreamRecord(String userId, String liveId) {
        String sql = "INSERT INTO live_record (user_id, live_id, start_time) VALUES (:userId, :liveId, :startTime)";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("liveId", liveId);
        params.put("startTime", new Timestamp(System.currentTimeMillis()));

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder, new String[]{"id"});
            return keyHolder.getKey().longValue();
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateLiveStreamRecord(String liveId,int viewers,int totalQuantity,int totalFigures) {
        String sql = "UPDATE live_record " +
                "SET viewers = :viewers, end_time = :endTime, sales_quantity = :totalQuantity, sales_figures = :totalFigures " +
                "WHERE live_id = :liveId";
        Map<String, Object> params = new HashMap<>();
        params.put("viewers", viewers);
        params.put("endTime", new Timestamp(System.currentTimeMillis()));
        params.put("totalQuantity", totalQuantity);
        params.put("totalFigures", totalFigures);
        params.put("liveId", liveId);
        try {
            namedParameterJdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getTotalPriceAndQuantity(String liveId) {
        String sql = "SELECT COALESCE(SUM(total_price), 0) AS totalPrice, COALESCE(SUM(quantity), 0) AS totalQuantity " +
                "FROM `order` WHERE live_id = :liveId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("liveId", liveId);
        return namedParameterJdbcTemplate.queryForMap(sql, params);
    }

    @Override
    public List<LiveStreamRecord> findLiveStreamRecordsByUserId(int userId) {
        String sql = "SELECT * FROM live_record WHERE user_id = :userId ORDER BY start_time DESC";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(LiveStreamRecord.class));

    }
    @Override
    public Integer findLiveRecordIdByLiveId(String liveId) {
        String sql = "SELECT id FROM live_record WHERE live_id = :liveId";

        Map<String, Object> params = new HashMap<>();
        params.put("liveId", liveId);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void saveSatisfactionRecord(SatisfactionDto satisfactionDto,Integer liveRecordId) {
        String sql = "INSERT INTO satisfaction (user_id, live_id, score,comment) VALUES (:userId, :liveId, :score,:comment)";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", satisfactionDto.getUserId());
        params.put("liveId", liveRecordId);
        params.put("score", satisfactionDto.getScore());
        params.put("comment", satisfactionDto.getComment());
        try {
            namedParameterJdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to insert satisfaction record", e);
        }
    }

    @Override
    public List<Satisfaction> findSatisfactionRecordByLiveId(Long liveId) {
        String sql = "SELECT \n" +
                "    s.id AS satisfactionId,\n" +
                "    s.score,\n" +
                "    s.comment,\n" +
                "    s.created_at AS createdTime,\n" +
                "    u.name,\n" +
                "    u.image\n" +
                "FROM \n" +
                "    satisfaction s\n" +
                "JOIN \n" +
                "    `user` u ON s.user_id = u.id\n" +
                "WHERE \n" +
                "    s.live_id = :liveId\n" +
                "ORDER BY s.id DESC;";
        Map<String, Object> params = new HashMap<>();
        params.put("liveId", liveId);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Satisfaction.class));
    }

    @Override
    public List<Satisfaction> findSatisfactionRecordByUserId(Long userId) {
        String sql = "SELECT " +
                "    s.id AS satisfactionId, " +
                "    s.score, " +
                "    s.comment, " +
                "    s.created_at AS createdTime, " +
                "    su.name , " +
                "    su.image " +
                "FROM " +
                "    live_record lr " +
                "JOIN " +
                "    satisfaction s ON lr.id = s.live_id " +
                "JOIN " +
                "    `user` su ON s.user_id = su.id " +
                "WHERE " +
                "    lr.user_id = :userId " +
                "ORDER BY " +
                "    s.created_at DESC;";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Satisfaction.class));
    }

    @Override
    public LiveSummary findLiveSummaryByUserId(Long userId) {
        String sql = "SELECT " +
                "    COUNT(lr.id) AS liveCount, " +
                "    COALESCE(AVG(s.score), 0) AS averageScore, " +
                "    COUNT(s.comment) AS commentCount " +
                "FROM " +
                "    live_record lr " +
                "LEFT JOIN " +
                "    satisfaction s ON lr.id = s.live_id " +
                "WHERE " +
                "    lr.user_id = :userId;";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(LiveSummary.class));
    }

}
