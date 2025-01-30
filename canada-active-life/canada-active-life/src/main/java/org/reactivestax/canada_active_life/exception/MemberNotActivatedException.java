package org.reactivestax.canada_active_life.exception;

public class MemberNotActivatedException extends RuntimeException {
    public MemberNotActivatedException(String message) {
        super(message);
    }
}
