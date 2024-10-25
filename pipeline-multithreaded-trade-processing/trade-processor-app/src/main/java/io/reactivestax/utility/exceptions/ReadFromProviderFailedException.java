package io.reactivestax.utility.exceptions;

public class ReadFromProviderFailedException extends RuntimeException {
    public ReadFromProviderFailedException(InterruptedException e) {
        System.out.println("ReadFromQueueFailedException e.getMessage() = " + e.getMessage());
    }
}
