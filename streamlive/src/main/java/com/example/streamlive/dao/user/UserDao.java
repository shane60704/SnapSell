package com.example.streamlive.dao.user;

import com.example.streamlive.dto.UserDto;

public interface UserDao {
    Integer createNativeUser(UserDto userDto);
    UserDto getNativeUserByEmailAndProvider(String email);
    UserDto getUserById(int userId);
}
