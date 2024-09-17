package com.example.streamlive.controller;

import com.example.streamlive.dto.ErrorResponseDto;
import com.example.streamlive.dto.UserDto;
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
