package com.example.streamlive.exception.custom;

public class DuplicatedEmailException extends RuntimeException{
    public DuplicatedEmailException(String message) {
        super(message);
    }
}
