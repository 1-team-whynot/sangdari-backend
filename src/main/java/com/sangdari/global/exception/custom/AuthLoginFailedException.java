package com.sangdari.global.exception.custom;

public class AuthLoginFailedException extends RuntimeException {
    public AuthLoginFailedException() {
        super("이메일 또는 비밀번호를 확인해주세요.");
    }
}
