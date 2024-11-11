package io.reactivestax.utility.exceptions;

public class ReadFromProviderFailedException extends RuntimeException {
    public ReadFromProviderFailedException() {
        System.out.println("Read From Queue Failed Exception");
    }
}
