package com.sangdari.global.errors.custom;

public class DuplicatedUserException extends RuntimeException {

    public DuplicatedUserException(String message) {
        super(message);
    }
}
