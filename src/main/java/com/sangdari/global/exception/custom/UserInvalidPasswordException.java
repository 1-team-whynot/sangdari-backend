package com.sangdari.global.exception.custom;

public class UserInvalidPasswordException extends RuntimeException {
    public UserInvalidPasswordException() {
        super("비밀번호 형식이 올바르지 않습니다.");
    }
}
