package com.sangdari.global.exception.custom;

public class ReservationAlreadyCompletedException extends RuntimeException {
    public ReservationAlreadyCompletedException(String message) {
        super(message);
    }
}
