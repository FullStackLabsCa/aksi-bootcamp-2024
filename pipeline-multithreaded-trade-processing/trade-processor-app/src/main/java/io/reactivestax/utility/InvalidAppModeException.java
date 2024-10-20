package io.reactivestax.utility;

public class InvalidAppModeException extends RuntimeException {
    public InvalidAppModeException(String s) {
        System.out.println(s);
    }
}
