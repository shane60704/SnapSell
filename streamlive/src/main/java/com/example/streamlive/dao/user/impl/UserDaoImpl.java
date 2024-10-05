package com.example.streamlive.dao.user.impl;

import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.dto.UserDto;
import com.example.streamlive.model.Agent;
import com.example.streamlive.model.Client;
import com.example.streamlive.model.rowmapper.UserRowMapper;
import com.example.streamlive.model.user.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer createNativeUser(UserDto userDto){
        try {
            String sql = "INSERT INTO user (name,email,password,provider) VALUES (:name,:email,:password,:provider)";
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            Map<String, Object> map = new HashMap<>();
            map.put("name", userDto.getName());
            map.put("email", userDto.getEmail());
            map.put("password", encodedPassword);
            map.put("provider", userDto.getProvider());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            SqlParameterSource paramSource = new MapSqlParameterSource(map);
            namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
            return keyHolder.getKey().intValue();
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDto getNativeUserByEmailAndProvider(String email){
        try {
            String sql = "SELECT * FROM user WHERE email = :email AND provider = :provider";
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("provider", "native");
            SqlParameterSource paramSource = new MapSqlParameterSource(map);
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDto getUserById(int userId) {
        String sql = "SELECT * FROM user WHERE id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (DataAccessException e) {
            throw e;
        }
    }

    @Override
    public Client getClientById(int clientId) {
        String sql = "SELECT id,name FROM user WHERE id = :clientId";
        Map<String, Object> map = new HashMap<>();
        map.put("clientId", clientId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(Client.class));
        } catch (DataAccessException e) {
            throw e;
        }
    }

    @Override
    public Agent getAgentById(int agentId) {
        String sql = "SELECT id,name FROM user WHERE id = :agentId";
        Map<String, Object> map = new HashMap<>();
        map.put("agentId", agentId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(Agent.class));
        } catch (DataAccessException e) {
            throw e;
        }
    }

    @Override
    public UserInfo getUserInfoById(int userId) {
        String sql = "SELECT id,name,email,image FROM user WHERE id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserInfo.class));
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        String sql = "SELECT id,name,email,image FROM user WHERE id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserInfo.class));
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public User findUserStatisticsById(Long userId) {
        String sql = "SELECT u.id , u.name, u.email, u.image, " +
                "SUM(lr.viewers) AS totalViewers, " +
                "SUM(lr.sales_quantity) AS totalQuantity, " +
                "SUM(lr.sales_figures) AS totalFigures, " +
                "AVG(s.score) AS averageScore " +
                "FROM user u " +
                "LEFT JOIN live_record lr ON u.id = lr.user_id " +
                "LEFT JOIN satisfaction s ON lr.id = s.live_id " +
                "WHERE u.id = :userId " +
                "GROUP BY u.id, u.name, u.email, u.image";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new UserRowMapper());
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public AgentProfile getAgentProfileById(Long userId) {
        String sql = "SELECT u.id , u.name, u.image, u.background_image AS backgroundImage, u.description, u.followers,  " +
                "SUM(lr.viewers) AS totalViewers, " +
                "SUM(lr.sales_quantity) AS totalQuantity, " +
                "SUM(lr.sales_figures) AS totalFigures, " +
                "AVG(s.score) AS averageScore " +
                "FROM user u " +
                "LEFT JOIN live_record lr ON u.id = lr.user_id " +
                "LEFT JOIN satisfaction s ON lr.id = s.live_id " +
                "WHERE u.id = :userId " +
                "GROUP BY u.id, u.name, u.image, u.background_image, u.description, u.followers";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(AgentProfile.class));
    }

    @Override
    public ClientProfile getClientProfileById(Long userId) {
        String sql = "SELECT u.id, u.name, u.image, u.background_image AS backgroundImage, u.description, u.followers, " +
                "COUNT(DISTINCT d.product_id) AS totalProducts, " +
                "AVG(o.score) AS averageScore " +
                "FROM user u " +
                "LEFT JOIN Delegation d ON u.id = d.client_id " +
                "LEFT JOIN `order` o ON d.product_id = o.product_id " +
                "WHERE u.id = :userId " +
                "GROUP BY u.id, u.name, u.image, u.background_image, u.description, u.followers";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(ClientProfile.class));
    }

    @Override
    public PersonalProfile getPersonalProfileById(Long userId) {
        String sql = "SELECT u.id, u.name, u.image, u.background_image AS backgroundImage, u.description, u.followers, " +
                "SUM(lr.viewers) AS totalViewers, " +
                "SUM(lr.sales_quantity) AS totalQuantity, " +
                "SUM(lr.sales_figures) AS totalFigures, " +
                "AVG(s.score) AS averageScore, " +
                "COUNT(DISTINCT d.product_id) AS totalProducts, " +
                "AVG(o.score) AS averageProductScore, " +
                "(SELECT COUNT(*) FROM delegation d WHERE d.agent_id = u.id) AS totalDelegationCount " +
                "FROM user u " +
                "LEFT JOIN live_record lr ON u.id = lr.user_id " +
                "LEFT JOIN satisfaction s ON lr.id = s.live_id " +
                "LEFT JOIN delegation d ON u.id = d.client_id " +
                "LEFT JOIN `order` o ON d.product_id = o.product_id " +
                "WHERE u.id = :userId " +
                "GROUP BY u.id, u.name, u.image, u.background_image, u.description, u.followers";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(PersonalProfile.class));
    }

    @Override
    public List<User> findAllUserStatisticsExceptCurrentUser(Long userId,String sortBy, String sortOrder,int limit, int offset) {
        String sql = "SELECT u.id , u.name, u.email, u.image, " +
                "SUM(lr.viewers) AS totalViewers, " +
                "SUM(lr.sales_quantity) AS totalQuantity, " +
                "SUM(lr.sales_figures) AS totalFigures, " +
                "AVG(s.score) AS averageScore, " +
                "(SELECT COUNT(*) FROM delegation d WHERE d.agent_id = u.id) AS totalDelegationCount " +
                "FROM user u " +
                "LEFT JOIN live_record lr ON u.id = lr.user_id " +
                "LEFT JOIN satisfaction s ON lr.id = s.live_id " +
                "WHERE u.id != :userId " +
                "GROUP BY u.id, u.name, u.email, u.image "+
                "ORDER BY " + sortBy + " " + sortOrder + " " +
                "LIMIT :limit OFFSET :offset";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("sortBy", sortBy);
        params.put("sortOrder", sortOrder);
        params.put("limit", limit);
        params.put("offset", offset);
        return namedParameterJdbcTemplate.query(sql, params, new UserRowMapper());
    }

    @Override
    public List<User> findUserStatisticsExceptCurrentUserByKeyword(Long userId,String keyword, String sortBy, String sortOrder,int limit, int offset) {
        String sql = "SELECT u.id , u.name, u.email, u.image, " +
                "SUM(lr.viewers) AS totalViewers, " +
                "SUM(lr.sales_quantity) AS totalQuantity, " +
                "SUM(lr.sales_figures) AS totalFigures, " +
                "AVG(s.score) AS averageScore, " +
                "(SELECT COUNT(*) FROM delegation d WHERE d.agent_id = u.id) AS totalDelegationCount " +
                "FROM user u " +
                "LEFT JOIN live_record lr ON u.id = lr.user_id " +
                "LEFT JOIN satisfaction s ON lr.id = s.live_id " +
                "WHERE u.id != :userId " +
                "AND u.name LIKE CONCAT('%', :keyword, '%') " +
                "GROUP BY u.id, u.name, u.email, u.image " +
                "ORDER BY " + sortBy + " " + sortOrder + " " +
                "LIMIT :limit OFFSET :offset";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("keyword", keyword);
        params.put("limit", limit);
        params.put("offset", offset);
        return namedParameterJdbcTemplate.query(sql, params, new UserRowMapper());
    }

    @Override
    public int updateUserBackgroundImageById(Long userId,String backgroundImagePath) {
        String sql = "UPDATE user SET background_image = :backgroundImagePath WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        params.put("backgroundImagePath", backgroundImagePath);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public int updateUserImageById(Long userId,String imagePath) {
        String sql = "UPDATE user SET image = :imagePath WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        params.put("imagePath", imagePath);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public int updateUserDescriptionById(Long userId,String description) {
        String sql = "UPDATE user SET description = :description WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", userId);
        params.put("description", description);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<AgentProfile> getTop3AgentByTotalViewers() {
        String sql = "SELECT u.id , u.name, u.image, u.background_image AS backgroundImage, u.description, u.followers, " +
                "SUM(lr.viewers) AS totalViewers, " +
                "SUM(lr.sales_quantity) AS totalQuantity, " +
                "SUM(lr.sales_figures) AS totalFigures, " +
                "AVG(s.score) AS averageScore " +
                "FROM user u " +
                "LEFT JOIN live_record lr ON u.id = lr.user_id " +
                "LEFT JOIN satisfaction s ON lr.id = s.live_id " +
                "GROUP BY u.id, u.name, u.image, u.background_image, u.description, u.followers " +
                "ORDER BY SUM(lr.viewers) DESC " +
                "LIMIT 5";
        return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AgentProfile.class));
    }

    @Override
    public String getUserImageById(String userId) {
        String sql = "SELECT image FROM user WHERE id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
    }
}
