package com.example.streamlive.exception.custom;

public class OrderCreationFailedException extends RuntimeException {
    public OrderCreationFailedException(String message) {
        super(message);
    }
}
