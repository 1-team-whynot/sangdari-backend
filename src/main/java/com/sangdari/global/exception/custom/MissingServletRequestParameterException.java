package com.sangdari.global.exception.custom;

public class MissingServletRequestParameterException extends RuntimeException {
    public MissingServletRequestParameterException(String message) {
        super(message);
    }
}
