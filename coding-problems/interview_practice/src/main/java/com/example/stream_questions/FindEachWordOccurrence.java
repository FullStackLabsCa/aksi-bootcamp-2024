package com.example.stream_questions;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FindEachWordOccurrence {

    private static void findOccurrence(String testString){
        String[] words = testString.trim().split(" ");

        Arrays.stream(words)
                .collect(Collectors.groupingBy(x -> x.toLowerCase()))
                .forEach((key, value) ->
                        System.out.println(key + " : " + value.size()));
    }

    public static void main(String[] args) {
        findOccurrence("I am learning Streams API in java Java");
    }
}
