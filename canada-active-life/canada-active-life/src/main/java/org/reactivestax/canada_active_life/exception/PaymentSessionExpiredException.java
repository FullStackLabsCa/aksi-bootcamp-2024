package org.reactivestax.canada_active_life.exception;

public class PaymentSessionExpiredException extends RuntimeException {
    public PaymentSessionExpiredException(String message) {
        super(message);
    }
}
