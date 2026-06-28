package com.sangdari.global.exception.custom;

public class UserEmailDuplicatedException extends RuntimeException {
    public UserEmailDuplicatedException() {
        super("이미 가입된 이메일입니다.");
    }
}
