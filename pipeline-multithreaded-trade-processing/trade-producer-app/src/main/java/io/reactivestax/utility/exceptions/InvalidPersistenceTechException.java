package io.reactivestax.utility.exceptions;

public class InvalidPersistenceTechException extends RuntimeException {
    public InvalidPersistenceTechException() {
        System.out.println("Invalid persistence technology");
    }
}
