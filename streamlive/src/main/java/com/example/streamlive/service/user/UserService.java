package com.example.streamlive.service.user;

import com.example.streamlive.dto.UserDto;

import java.util.Map;

public interface UserService {
    Map<String, Object> signIn(Map<String, Object> signInRequest);
    Map<String, Object> signUp(UserDto userDto);
}
