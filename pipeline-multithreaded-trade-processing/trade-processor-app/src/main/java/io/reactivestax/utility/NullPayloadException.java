package io.reactivestax.utility;

public class NullPayloadException extends RuntimeException {
    public NullPayloadException(String s) {
        System.out.println("NullPayloadException: "+s);
    }
}
