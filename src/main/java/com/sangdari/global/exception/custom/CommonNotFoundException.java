package com.sangdari.global.exception.custom;

public class CommonNotFoundException extends RuntimeException {
    public CommonNotFoundException(String message) {
        super(message);
    }
}
