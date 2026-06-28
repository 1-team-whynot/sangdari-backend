package com.sangdari.global.errors;

public class ReservationAlreadyCompletedException extends RuntimeException {
    public ReservationAlreadyCompletedException(String message) {
        super(message);
    }
}
