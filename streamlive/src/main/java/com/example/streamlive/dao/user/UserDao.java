package com.example.streamlive.dao.user;

import com.example.streamlive.dto.UserDto;
import com.example.streamlive.model.Agent;
import com.example.streamlive.model.Client;

public interface UserDao {
    Integer createNativeUser(UserDto userDto);
    UserDto getNativeUserByEmailAndProvider(String email);
    UserDto getUserById(int userId);
    Client getClientById(int clientId);
    Agent getAgentById(int agentId);
}
