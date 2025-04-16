package com.example.stream_questions;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FindFirstRepeatedChar {

    public static Character findFirstRepeatedCharacter(String testString) {
        return IntStream.range(0, testString.length())
                .mapToObj(index -> testString.toCharArray()[index])
                .collect(Collectors.groupingBy(
                        x -> x,
                        LinkedHashMap::new,
                        Collectors.counting()
                )).entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }

    public static void main(String[] args) {
//        System.out.println(findFirstNonRepeatedCharacter("Hello World"));
        System.out.println(findFirstRepeatedCharacter("stress"));
    }
}
