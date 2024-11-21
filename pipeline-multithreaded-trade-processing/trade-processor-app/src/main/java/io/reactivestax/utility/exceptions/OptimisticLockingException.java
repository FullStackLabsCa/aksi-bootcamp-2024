package io.reactivestax.utility.exceptions;

public class OptimisticLockingException extends Throwable {
    public OptimisticLockingException(String message) {

        System.out.println(message);
    }
}
