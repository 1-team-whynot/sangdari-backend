package com.sangdari.global.exception.custom;

public class UserWithdrawBlockedException extends RuntimeException {
    public UserWithdrawBlockedException() {
        super("진행 중인 예약 또는 미정산 건이 있어 탈퇴할 수 없습니다.");
    }
}
