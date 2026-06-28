package com.sangdari.global.exception.custom;

public class PaymentTypeInvalidException extends RuntimeException {
    public PaymentTypeInvalidException(String message) {
        super(message);
    }
}
