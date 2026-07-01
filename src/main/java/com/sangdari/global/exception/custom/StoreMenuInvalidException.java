package com.sangdari.global.exception.custom;

public class StoreMenuInvalidException extends RuntimeException{
    public StoreMenuInvalidException() {
        super("선택한 메뉴가 해당 업체의 메뉴가 아닙니다.");
    }
}
