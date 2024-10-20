package io.reactivestax.utility.exceptions;

public class OptimisticLockingException extends RuntimeException {
    public OptimisticLockingException(String message) {

        System.out.println(message);
    }
}
