package io.reactivestax.utility.exceptions;

public class TradeCreationFailedException extends RuntimeException {
    public TradeCreationFailedException(String s) {
        System.out.println("TradeCreationFailedException :" + s);
    }
}
