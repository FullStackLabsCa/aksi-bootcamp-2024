package io.reactivestax.utility.exceptions;

public class InvalidPersistenceTechException extends RuntimeException {
    public InvalidPersistenceTechException(String invalidPersistenceTechnology) {
        System.out.println("invalidPersistenceTechnology = " + invalidPersistenceTechnology);
    }
}
