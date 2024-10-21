package io.reactivestax.utility.exceptions;

public class OptimisticLockingExceptionThrowable extends Throwable {
    public OptimisticLockingExceptionThrowable(String message) {

        System.out.println(message);
    }
}
