package com.sangdari.global.errors;

public class MissingServletRequestParameterException extends RuntimeException {
    public MissingServletRequestParameterException(String message) {
        super(message);
    }
}
