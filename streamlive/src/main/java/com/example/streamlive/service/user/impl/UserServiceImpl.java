package com.example.streamlive.service.user.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.dto.UserDto;
import com.example.streamlive.exception.custom.DuplicatedEmailException;
import com.example.streamlive.exception.custom.InvalidEmailFormatException;
import com.example.streamlive.exception.custom.InvalidProviderException;
import com.example.streamlive.exception.custom.LoginFailedException;
import com.example.streamlive.model.user.AgentProfile;
import com.example.streamlive.model.user.User;
import com.example.streamlive.service.user.UserService;
import com.example.streamlive.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${aws.s3.baseurl}")
    private String s3BaseUrl;

    private final AmazonS3 s3Client;
    private final JwtUtil jwtUtil;
    private final UserDao userDao;

    @Override
    public Map<String, Object> signIn(Map<String, Object> signInRequest) {
        String provider = (String) signInRequest.get("provider");
        switch (provider.toLowerCase()) {
            case "native":
                String email = (String) signInRequest.get("email");
                if (!checkoutEmailFormat(email)) {
                    throw new InvalidEmailFormatException("Invalid email format");
                }
                UserDto nativeUser = userDao.getNativeUserByEmailAndProvider(email);
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if (nativeUser == null || !passwordEncoder.matches((String) signInRequest.get("password"), nativeUser.getPassword())) {
                    throw new LoginFailedException("Invalid email or password");
                }
                return generateAuthResponse(userDao.getUserById(nativeUser.getId().intValue()));
//            case "twm":
//                String accessToken = getAccessToken(signInRequest.get("accessToken").toString());
//                Map<String, Object> userProfileMap = getTwmUserProfile(accessToken);
//                UserDto twmUser = userRepository.getTwmUserByEmailAndProvider(userProfileMap.get("email").toString());
//                if (twmUser == null) {
//                    int userId = userRepository.createTwmUser(userProfileMap.get("email").toString());
//                    return generateAuthResponse(userRepository.getUserById(userId));
//                }else{
//                    return generateAuthResponse(userRepository.getUserById(twmUser.getId().intValue()));
//                }
            default:
                throw new InvalidProviderException("Invalid provider");
        }
    }

    @Override
    public Map<String, Object> signUp(UserDto userDto) {
        if (userDao.getNativeUserByEmailAndProvider(userDto.getEmail()) != null) {
            throw new DuplicatedEmailException("Email already exists");
        }
        int userId = userDao.createNativeUser(userDto);
        return generateAuthResponse(userDao.getUserById(userId));
    }

    @Override
    public User getCurrentUserStatistics(Long currentUserId) {
        return userDao.findUserStatisticsById(currentUserId);
    }

    @Override
    public Object getUserProfile(Long currentUserId,String type) {
        switch (type.toLowerCase()) {
            case "agent":
                return userDao.getAgentProfileById(currentUserId);
            case "client":
                return userDao.getClientProfileById(currentUserId);
            case "all":
                return userDao.getPersonalProfileById(currentUserId);
            default:
                throw new IllegalArgumentException("Invalid profile type: " + type);
        }
    }

    @Override
    public Map<String,Object> getOtherUsersStatistics(Long currentUserId, String sortBy, String sortOrder,int paging) {
        int pageSize = 100;
        int limit = 101;
        int offset = paging * pageSize;
        List<User> users = userDao.findAllUserStatisticsExceptCurrentUser(currentUserId, sortBy, sortOrder, limit, offset);
        int userCount = users.size();
        Integer nextPaging = null;
        if (userCount > pageSize) {
            nextPaging = paging + 1;
            userCount = pageSize;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        if (nextPaging != null) {
            result.put("next_paging", nextPaging);
        }
        return result;
    }

    @Override
    public Map<String, Object> searchUserStatisticsExceptCurrentUserByKeyword(Long currentUserId, String keyword, String sortBy, String sortOrder, int paging) {
        int pageSize = 100;
        int limit = 101;
        int offset = paging * pageSize;
        List<User> users = userDao.findUserStatisticsExceptCurrentUserByKeyword(currentUserId, keyword, sortBy, sortOrder, limit, offset);
        int userCount = users.size();
        Integer nextPaging = null;
        if (userCount > pageSize) {
            nextPaging = paging + 1;
            userCount = pageSize;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        if (nextPaging != null) {
            result.put("next_paging", nextPaging);
        }
        return result;
    }

    @Override
    public Boolean updateProfileBackgroundImage(Long userId, MultipartFile file)  {
        try {
            String backgroundImagePath = uploadProductImage(file);
            int affectedRows = userDao.updateUserBackgroundImageById(userId, backgroundImagePath);
            return affectedRows > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean updateUserImage(Long userId, MultipartFile file) {
        try {
            String imagePath = uploadProductImage(file);
            int affectedRows = userDao.updateUserImageById(userId, imagePath);
            return affectedRows > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Boolean updateUserDescription (Long userId,String description ) {
        int affectedRows = userDao.updateUserDescriptionById(userId, description);
        return affectedRows > 0;
    }

    @Override
    public List<AgentProfile> getTop3AgentProfiles() {
        return userDao.getTop3AgentByTotalViewers();
    }

    @Nullable
    @Override
    public Map solveJwt(String token) {
        return jwtUtil.isTokenValid(token) ? jwtUtil.getClaims(token) : null;
    }

    private Boolean checkoutEmailFormat(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (email == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private Map<String, Object> generateAuthResponse(UserDto userDto) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", userDto.getId());
        userInfo.put("name", userDto.getName());
        userInfo.put("provider", userDto.getProvider());
        userInfo.put("email", userDto.getEmail());
        userInfo.put("image", userDto.getImage());
        String jwtToken = jwtUtil.getToken(userInfo);
        int expiresIn = jwtUtil.getExpiration();
        response.put("accessToken", jwtToken);
        response.put("accessExpired", expiresIn);
        response.put("user", userInfo);
        return response;
    }

    public String uploadProductImage(MultipartFile file) throws IOException {
        File convertedFile = convertMultiPartToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(getBucketName(), fileName, convertedFile));
        convertedFile.delete();
        return "https://" + s3BaseUrl + "/" + fileName;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private String getBucketName() {
        return s3BaseUrl.split("\\.")[0];
    }


}
