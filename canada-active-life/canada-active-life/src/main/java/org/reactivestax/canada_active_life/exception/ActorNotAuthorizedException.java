package org.reactivestax.canada_active_life.exception;

public class ActorNotAuthorizedException extends RuntimeException {
    public ActorNotAuthorizedException(String message) {
        super(message);
    }
}
