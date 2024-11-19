package io.reactivestax.utility.exceptions;

public class ChunkProcessorException extends RuntimeException {
    public ChunkProcessorException(String s) {
        System.out.println("Invalid Chunk Path Exception : "+s);
    }
}
