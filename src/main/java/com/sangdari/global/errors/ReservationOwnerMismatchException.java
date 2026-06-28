package com.sangdari.global.errors;

public class ReservationOwnerMismatchException extends RuntimeException {
    public ReservationOwnerMismatchException(String message) {
        super(message);
    }
}
