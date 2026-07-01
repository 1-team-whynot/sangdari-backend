package com.sangdari.global.exception.custom;

public class UserPhoneDuplicatedException extends RuntimeException {
    public UserPhoneDuplicatedException() {
        super("이미 가입된 번호입니다.");
    }
}
