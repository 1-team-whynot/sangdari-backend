package com.sangdari.global.exception.custom;

public class StoreMenuRequiredException extends RuntimeException{
    public StoreMenuRequiredException() {
        super(" 메뉴를 1개 이상 선택해주세요.");
    }
}
