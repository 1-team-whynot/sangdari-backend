package com.sangdari.global.exception.custom;

public class UserCurrentPasswordMismatchException extends RuntimeException {
    public UserCurrentPasswordMismatchException() {
        super("현재 비밀번호가 올바르지 않습니다.");
    }
}