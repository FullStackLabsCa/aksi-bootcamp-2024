package com.example.stream_questions;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FindOccurrenceOfEachCharacter {

    private static void findOccurrenceOfEachCharacter(String word){

        char[] characters = word.trim().toCharArray();

        IntStream
                .range(0, characters.length)
                .mapToObj(index -> characters[index])
                .collect(Collectors.groupingBy(x -> x))
                .forEach((key, value) -> System.out.println(key + "=" + value.size()));

    }

    public static void main(String[] args) {
        findOccurrenceOfEachCharacter("Mississippim");
    }
}
