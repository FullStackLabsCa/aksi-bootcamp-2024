package io.reactivestax.utility;

public class InvalidFilePathException extends RuntimeException {
    public InvalidFilePathException(String s) {
        System.out.println("Invalid File Path Exception : "+s);
    }
}
