package com.example.streamlive.dao.user.impl;

import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.dto.UserDto;
import lombok.RequiredArgsConstructor;
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
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer createNativeUser(UserDto userDto){
        try {
            String sql = "INSERT INTO users (name,email,password,provider) VALUES (:name,:email,:password,:provider)";
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
            String sql = "SELECT * FROM users WHERE email = :email AND provider = :provider";
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
        String sql = "SELECT * FROM users WHERE id = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        SqlParameterSource paramSource = new MapSqlParameterSource(map);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, paramSource, new BeanPropertyRowMapper<>(UserDto.class));
        } catch (DataAccessException e) {
            throw e;
        }
    }


}
