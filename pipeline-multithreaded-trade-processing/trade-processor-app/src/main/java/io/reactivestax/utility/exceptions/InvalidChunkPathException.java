package io.reactivestax.utility.exceptions;

public class InvalidChunkPathException extends RuntimeException {
    public InvalidChunkPathException(String s) {
        System.out.println("Invalid Chunk Path Exception : "+s);
    }
}
