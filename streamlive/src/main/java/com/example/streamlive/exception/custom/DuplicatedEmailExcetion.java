package com.example.streamlive.exception.custom;

public class DuplicatedEmailExcetion extends RuntimeException{
    public DuplicatedEmailExcetion(String message) {
        super(message);
    }
}
