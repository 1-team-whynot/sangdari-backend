package com.sangdari.global.exception.custom;

public class StoreAlreadyReservedException extends RuntimeException {
    public StoreAlreadyReservedException() {
        super("이미 예약된 업체입니다.");
    }
}
