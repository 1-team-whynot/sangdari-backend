package com.sangdari.global.exception.custom;

public class ReservationRequiredMissingException extends RuntimeException {
    public ReservationRequiredMissingException() {
        super("견적 요청 정보를 올바르게 입력해주세요.");
    }
}
