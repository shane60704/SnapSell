package com.example.streamlive.exception;

import com.example.streamlive.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public class GlobalExceptionHandler {
    @Order(100)
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleException(Exception e) {
        log.warn(e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(ErrorResponseDto.error("something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
