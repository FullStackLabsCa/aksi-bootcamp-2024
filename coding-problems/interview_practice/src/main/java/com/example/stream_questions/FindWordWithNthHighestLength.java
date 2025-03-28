package com.example.stream_questions;

import java.util.Arrays;
import java.util.Comparator;

public class FindWordWithNthHighestLength {

    private static void findWordWithNthHighestLength(String testString, int n){

        String[] words = testString.trim().split(" ");

        String word = Arrays.stream(words)
                .sorted(Comparator.comparing(String::length))
                .skip(words.length - n)
                .findFirst()
                .get();

        System.out.println("Word Length = " + word.length());

    }

    public static void main(String[] args) {
        findWordWithNthHighestLength("I am learnign Streams API in java", 2);
    }
}
