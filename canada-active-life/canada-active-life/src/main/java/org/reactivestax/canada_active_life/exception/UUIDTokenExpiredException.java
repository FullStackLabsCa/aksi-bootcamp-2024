package org.reactivestax.canada_active_life.exception;

public class UUIDTokenExpiredException extends RuntimeException {
    public UUIDTokenExpiredException(String message) {
        super(message);
    }
}
