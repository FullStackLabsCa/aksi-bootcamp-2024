package org.reactivestax.ems.exception;

public class MaxOTPFailureCountReachedException extends RuntimeException {
    public MaxOTPFailureCountReachedException(String message) {
        super(message);
    }
}
