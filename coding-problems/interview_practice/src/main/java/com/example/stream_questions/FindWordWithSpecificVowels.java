package com.example.stream_questions;

import java.util.Arrays;
import java.util.stream.IntStream;

public class FindWordWithSpecificVowels {

    private static void findWordWithSpecificNumOfVowels(String testString, int numOfVowels){
        String[] words = testString.trim().split(" ");

        Arrays.stream(words)
                .filter((word) -> {
                    long size = IntStream.range(0, word.length()).mapToObj(index -> word.toCharArray()[index])
                            .filter((character -> "aeiou".contains(character.toString().toLowerCase())))
                            .count();
                    return (size == numOfVowels);
                })
                .forEach(System.out::println);
    }

    public static void main(String[] args) {
        findWordWithSpecificNumOfVowels("I am learning Streams API in Java", 2);
    }
}
