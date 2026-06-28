package com.sangdari.global.exception.custom;

public class UserPasswordMismatchException extends RuntimeException {
    public UserPasswordMismatchException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
