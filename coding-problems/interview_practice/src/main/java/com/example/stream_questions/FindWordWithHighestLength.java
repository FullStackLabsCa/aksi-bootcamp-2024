package com.example.stream_questions;

import java.util.Arrays;
import java.util.Comparator;

public class FindWordWithHighestLength {

    private static void findWordWithHighestLength(String testString){
        String[] words = testString.trim().replace(".", "").replace(",", "").split(" ");

        String maxLengthWord = "";
        for(String word : words){
            if(word.length() > maxLengthWord.length())
                maxLengthWord = word;
        }

        System.out.println("Max Length Word = " + maxLengthWord);
    }

    private static void findWordWithHighestLengthUsingStreams(String testString){
        String[] words = testString.trim().replace(".", "").replace(",", "").split(" ");

        String maxLengthString = Arrays.stream(words)
                .max(Comparator.comparing(String::length))
                .get();

        System.out.println("maxLengthString = " + maxLengthString);
    }

    public static void main(String[] args) {
        findWordWithHighestLength("I am learning Streams API in Java");
        findWordWithHighestLengthUsingStreams("I am learning Streams API in Java");
    }
}
