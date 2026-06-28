package com.sangdari.global.exception.custom;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }
}
