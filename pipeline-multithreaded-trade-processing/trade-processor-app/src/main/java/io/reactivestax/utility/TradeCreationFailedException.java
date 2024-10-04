package io.reactivestax.utility;

public class TradeCreationFailedException extends RuntimeException {
    public TradeCreationFailedException(String s) {
        System.out.println("TradeCreationFailedException :" + s);
    }
}
