package org.reactivestax.canada_active_life.exception;

public class FailedToSendOtpException extends RuntimeException {
    public FailedToSendOtpException(String message) {
        super(message);
    }
}
