package com.sangdari.global.exception.custom;

public class ReservationOwnerMismatchException extends RuntimeException {
    public ReservationOwnerMismatchException(String message) {
        super(message);
    }
}
