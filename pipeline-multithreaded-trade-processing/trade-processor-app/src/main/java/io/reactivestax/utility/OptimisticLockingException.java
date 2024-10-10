package io.reactivestax.utility;

public class OptimisticLockingException extends RuntimeException {
    public OptimisticLockingException(String message) {

        System.out.println(message);
    }
}
