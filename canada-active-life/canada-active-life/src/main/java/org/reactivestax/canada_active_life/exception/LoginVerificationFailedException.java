package org.reactivestax.canada_active_life.exception;

public class LoginVerificationFailedException extends RuntimeException {
    public LoginVerificationFailedException(String message) {
        super(message);
    }
}
