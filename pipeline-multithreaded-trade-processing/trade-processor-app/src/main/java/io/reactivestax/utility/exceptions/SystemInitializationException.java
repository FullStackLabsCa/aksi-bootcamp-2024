package io.reactivestax.utility.exceptions;

public class SystemInitializationException extends RuntimeException {
    public SystemInitializationException(String message) {
        System.out.println(message);
    }
}
