package com.example.stream_questions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.sort;

public class GroupAnagrams {

    public static void groupAnagrams(String[] strings) {
//        List<Map.Entry<String, int[]>> list = Arrays.stream(strings)
//                .map(String::toLowerCase)
//                .collect(Collectors.toMap(word -> word, word ->
//                        IntStream.range(0, word.length())
//                                .map(index -> word.toCharArray()[index])
//                                .sorted()
//                                .toArray()
//                ))
//                .entrySet().stream().toList();
//
//        list.stream()
//                .collect(Collectors.groupingBy(stringEntry -> Arrays.toString(stringEntry.getValue())))
//                .values()
//                .forEach(System.out::println);
//
        Arrays.stream(strings)
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(
                        word -> {
                            char[] chars = word.toCharArray();
                            Arrays.sort(chars);
                            return new String(chars);
                        }
                ))
                .values()
                .forEach(System.out::println);

    }

    public static void main(String[] args) {
        groupAnagrams(new String[]{"pat", "tap", "pan", "nap", "Team", "tree", "meat"});
        // Output Expected: [[pan, nap], [pat, tap], [Team, meat], [tree]]
    }
}
