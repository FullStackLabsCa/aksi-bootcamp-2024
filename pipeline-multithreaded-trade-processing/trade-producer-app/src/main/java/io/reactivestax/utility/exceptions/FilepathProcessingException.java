package io.reactivestax.utility.exceptions;

public class FilepathProcessingException extends RuntimeException {
    public FilepathProcessingException(String s) {
        System.out.println("Invalid Chunk Path Exception : "+s);
    }
}
