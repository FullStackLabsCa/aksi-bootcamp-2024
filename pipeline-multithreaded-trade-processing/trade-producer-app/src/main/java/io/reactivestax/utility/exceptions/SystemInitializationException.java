package io.reactivestax.utility.exceptions;

public class SystemInitializationException extends RuntimeException {
    public SystemInitializationException(String s) {
        System.out.println(s);
    }
}
