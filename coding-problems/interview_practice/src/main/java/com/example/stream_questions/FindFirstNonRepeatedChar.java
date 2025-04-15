package com.example.stream_questions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FindFirstNonRepeatedChar {

    public static Character findFirstNonRepeatedCharacter(String testString) {

        return IntStream.range(0, testString.length())
                .mapToObj(x -> testString.toCharArray()[x])
                .collect(Collectors.groupingBy(
                        x -> x,
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet().stream().filter(entry -> entry.getValue() == 1)
                .findFirst()
                .map(entry -> entry.getKey())
                .orElse(null);
    }

    public static void main(String[] args) {
//        System.out.println(findFirstNonRepeatedCharacter("Hello World"));
        System.out.println(findFirstNonRepeatedCharacter("stress"));
    }
}
