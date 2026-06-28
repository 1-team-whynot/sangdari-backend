package com.sangdari.global.errors;

public class DatabaseOperationFailedException extends RuntimeException {
    public DatabaseOperationFailedException(String message) {
        super(message);
    }
}
