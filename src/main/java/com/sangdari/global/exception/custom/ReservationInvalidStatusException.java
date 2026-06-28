package com.sangdari.global.exception.custom;

public class ReservationInvalidStatusException extends RuntimeException {
    public ReservationInvalidStatusException(String message) {
        super(message);
    }
}
