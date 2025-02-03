package org.reactivestax.canada_active_life.exception;

public class FailedToSendNotificationException extends RuntimeException {
    public FailedToSendNotificationException(String message) {
        super(message);
    }
}
