package com.sangdari.global.exception.custom;

public class StoreNotFoundException extends RuntimeException{
    public StoreNotFoundException() {
        super("요청한 업체 정보를 찾을 수 없습니다.");
    }
}
