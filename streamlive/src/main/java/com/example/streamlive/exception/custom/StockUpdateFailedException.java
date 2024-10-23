package com.example.streamlive.exception.custom;

public class StockUpdateFailedException extends RuntimeException  {
    public StockUpdateFailedException(String message) {
        super(message);
    }
}
