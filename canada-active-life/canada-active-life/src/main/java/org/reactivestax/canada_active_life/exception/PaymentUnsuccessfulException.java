package org.reactivestax.canada_active_life.exception;

public class PaymentUnsuccessfulException extends RuntimeException {
    public PaymentUnsuccessfulException(String message) {
        super(message);
    }
}
