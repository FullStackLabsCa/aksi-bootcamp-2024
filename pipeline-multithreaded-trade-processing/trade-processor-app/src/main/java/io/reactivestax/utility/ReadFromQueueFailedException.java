package io.reactivestax.utility;

public class ReadFromQueueFailedException extends RuntimeException {
    public ReadFromQueueFailedException(InterruptedException e) {
        System.out.println("ReadFromQueueFailedException e.getMessage() = " + e.getMessage());
    }
}
