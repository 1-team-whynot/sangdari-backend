package com.sangdari.global.errors;

public class PaymentOrderIdDuplicatedException extends RuntimeException {
    public PaymentOrderIdDuplicatedException(String message) {
        super(message);
    }
}
