package com.sangdari.global.errors;

public class ReservationInvalidStatusException extends RuntimeException {
    public ReservationInvalidStatusException(String message) {
        super(message);
    }
}
