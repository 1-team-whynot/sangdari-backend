package com.sangdari.global.errors;

public class ReservationAlreadyCanceledException extends RuntimeException {
    public ReservationAlreadyCanceledException(String message) {
        super(message);
    }
}
