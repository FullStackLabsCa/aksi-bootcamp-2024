package org.reactivestax.ems.exception;

public class MaxOTPGenerationCountReachedException extends RuntimeException {
    public MaxOTPGenerationCountReachedException(String message) {
        super(message);
    }
}
