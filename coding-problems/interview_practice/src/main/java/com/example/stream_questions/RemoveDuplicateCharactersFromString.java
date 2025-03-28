package com.example.stream_questions;

import java.util.stream.IntStream;

public class RemoveDuplicateCharactersFromString {

    private static void removeDuplicates(String testString){

        IntStream.range(0, testString.trim().length()).mapToObj(index -> testString.toCharArray()[index])
                .distinct()
                .forEach(System.out::print);

    }

    public static void main(String[] args) {
        removeDuplicates("dabcadefg");
    }
}
