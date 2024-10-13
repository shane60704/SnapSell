package com.example.streamlive.controller;

import com.example.streamlive.dto.ErrorResponseDto;
import com.example.streamlive.dto.UserDto;
import com.example.streamlive.dto.response.ApiResponse;
import com.example.streamlive.exception.custom.DuplicatedEmailExcetion;
import com.example.streamlive.exception.custom.InvalidEmailFormatException;
import com.example.streamlive.exception.custom.InvalidProviderException;
import com.example.streamlive.exception.custom.LoginFailedException;
import com.example.streamlive.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, Object> signInRequest) {
        return ResponseEntity.ok(userService.signIn(signInRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            ErrorResponseDto<List<String>> errorResponse = ErrorResponseDto.error(errors);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok(userService.signUp(userDto));
    }

    // 取得當前使用者的統計數據與基本資料
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserStatistics(@RequestParam Long currentUserId) {
        return ResponseEntity.ok(new ApiResponse<>(userService.getCurrentUserStatistics(currentUserId)));
    }

    // 取得其他使用者的統計數據與基本資料
    @GetMapping("/others")
    public ResponseEntity<?> getOtherUsersStatistics(@RequestParam Long currentUserId,
                                                     @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, // 默認排序欄位為 id (上架時間)
                                                     @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder, // 默認排序順序為desc (降序)
                                                     @RequestParam(value = "paging", defaultValue = "0") int paging) {
        return ResponseEntity.ok(new ApiResponse<>(userService.getOtherUsersStatistics(currentUserId, sortBy, sortOrder, paging)));
    }

    @GetMapping("search")
    public ResponseEntity<?> searchAgent(@RequestParam Long userId,
                                         @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                         @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                         @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
                                         @RequestParam(value = "paging", defaultValue = "0") int paging) {
        return ResponseEntity.ok(new ApiResponse<>(userService.searchUserStatisticsExceptCurrentUserByKeyword(userId, keyword, sortBy, sortOrder, paging)));
    }

    @GetMapping("/top-agent")
    public ResponseEntity<?> getTopAgent() {
        return ResponseEntity.ok(new ApiResponse<>(userService.getTop3AgentProfiles()));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId, @RequestParam("type") String type) {
        return ResponseEntity.ok(new ApiResponse<>(userService.getUserProfile(userId, type)));
    }

    @PutMapping("/{userId}/profile/background-image")
    public ResponseEntity<?> updateProfileBackgroundImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        return userService.updateProfileBackgroundImage(userId, file)
                ? ResponseEntity.ok(new ApiResponse<>("Success"))
                : new ResponseEntity<>(ErrorResponseDto.error("failed"), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{userId}/profile/profile-image")
    public ResponseEntity<?> updateProfileProfileImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        return userService.updateUserImage(userId, file)
                ? ResponseEntity.ok(new ApiResponse<>("Success"))
                : new ResponseEntity<>(ErrorResponseDto.error("failed"), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{userId}/profile/description")
    public ResponseEntity<?> updateProfileDescription(@PathVariable Long userId, @RequestBody String description) {
        return userService.updateUserDescription(userId, description)
                ? ResponseEntity.ok(new ApiResponse<>("Success"))
                : new ResponseEntity<>(ErrorResponseDto.error("failed"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/solve-jwt")
    public ResponseEntity<?> solveJwt(@RequestParam("token") String token){
        Map<String, Object> claims = userService.solveJwt(token);
        if (claims != null)
            return ResponseEntity.ok(claims);
        else
            return new ResponseEntity(ErrorResponseDto.error("Invalid JWT"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailFormatException(InvalidEmailFormatException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProviderException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailOrPasswordException(InvalidProviderException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailOrPasswordException(LoginFailedException ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicatedEmailExcetion.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidEmailOrPasswordException(DuplicatedEmailExcetion ex) {
        ErrorResponseDto<String> errorResponse = ErrorResponseDto.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

}
