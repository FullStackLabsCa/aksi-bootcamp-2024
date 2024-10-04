package io.reactivestax.utility;

public class InvalidChunkPathException extends RuntimeException {
    public InvalidChunkPathException(String s) {
        System.out.println("Invalid Chunk Path Exception : "+s);
    }
}
