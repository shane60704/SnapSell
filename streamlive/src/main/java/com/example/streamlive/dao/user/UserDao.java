package com.example.streamlive.dao.user;

import com.example.streamlive.dto.UserDto;
import com.example.streamlive.model.Agent;
import com.example.streamlive.model.Client;
import com.example.streamlive.model.user.*;

import java.util.List;

public interface UserDao {
    Integer createNativeUser(UserDto userDto);
    UserDto getNativeUserByEmailAndProvider(String email);
    UserDto getUserById(int userId);
    Client getClientById(int clientId);
    Agent getAgentById(int agentId);
    UserInfo getUserInfoById(int userId);
    User findUserStatisticsById(Long userId);
    List<User> findAllUserStatisticsExceptCurrentUser(Long userId, String sortBy, String sortOrder,int limit, int offset);
    UserInfo getUserInfoByUserId(Long userId);
    List<User> findUserStatisticsExceptCurrentUserByKeyword(Long userId,String keyword, String sortBy, String sortOrder,int limit, int offset);
    int updateUserBackgroundImageById(Long userId,String backgroundImagePath);
    int updateUserImageById(Long userId,String imagePath);
    int updateUserDescriptionById(Long userId,String description);
    AgentProfile getAgentProfileById(Long userId);
    ClientProfile getClientProfileById(Long userId);
    PersonalProfile getPersonalProfileById(Long userId);
    List<AgentProfile> getTop3AgentByTotalViewers();
}
