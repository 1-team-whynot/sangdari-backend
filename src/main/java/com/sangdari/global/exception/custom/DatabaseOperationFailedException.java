package com.sangdari.global.exception.custom;

public class DatabaseOperationFailedException extends RuntimeException {
    public DatabaseOperationFailedException(String message) {
        super(message);
    }
}
