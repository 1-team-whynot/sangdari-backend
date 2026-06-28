package com.sangdari.global.exception.custom;

public class ReservationAlreadyCanceledException extends RuntimeException {
    public ReservationAlreadyCanceledException(String message) {
        super(message);
    }
}
