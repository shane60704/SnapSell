package com.example.streamlive.service.user;

import com.example.streamlive.dto.UserDto;
import com.example.streamlive.model.user.AgentProfile;
import com.example.streamlive.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> signIn(Map<String, Object> signInRequest);
    Map<String, Object> signUp(UserDto userDto);
    User getCurrentUserStatistics(Long currentUserId);
    Map<String,Object> getOtherUsersStatistics(Long currentUserId, String sortBy, String sortOrder,int paging);
    Map<String,Object> searchUserStatisticsExceptCurrentUserByKeyword(Long currentUserId,String keyword,String sortBy, String sortOrder,int paging);
    Boolean updateProfileBackgroundImage(Long userId, MultipartFile file);
    Boolean updateUserImage(Long userId, MultipartFile file) ;
    Boolean updateUserDescription (Long userId,String description);
    Object getUserProfile(Long currentUserId,String type);
    List<AgentProfile> getTop3AgentProfiles();
    Map solveJwt(String token);
}
