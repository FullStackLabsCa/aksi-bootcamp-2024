package com.example.stream_questions;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MinOccurrencesOfCharacter {
    // Write a program check the minimum number of occurrences of a character in a given string
    // Character that occurred the min num of times

    private static void findMinOccurrenceOfCharacter(String testString){
        String stringWithoutSpaces = testString.replace(" ", "").trim();
        HashMap<Character, Long> characterCount = new HashMap<>();

        char[] charArray = stringWithoutSpaces.toCharArray();

        for(char character : charArray){
            if(characterCount.containsKey(character)){
                Long charCount = characterCount.get(character);
                charCount++;
                characterCount.replace(character, charCount);
            } else
                characterCount.put(character, 1L);
        }

        Long minCount = characterCount.values()
                .stream()
                .sorted()
                .distinct()
                .findFirst()
                .get();
        for(Map.Entry<Character, Long> entry : characterCount.entrySet()){
            if(entry.getValue().equals(minCount))
                System.out.println("character: " + entry.getKey() + " count: " + entry.getValue());
        }
    }

    private static void findMinOccurredCharacterUsingStreams(String testString){
        String stringWithoutSpaces = testString.replace(" ", "").trim();

        Map<Character, Long> mapWithCharCount =
                IntStream.range(0, stringWithoutSpaces.length()).mapToObj(index -> stringWithoutSpaces.toCharArray()[index])
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()));

        mapWithCharCount
                .entrySet().stream()
                .filter(characterLongEntry ->
                        characterLongEntry.getValue().equals(mapWithCharCount.values().stream().sorted().findFirst().get()))
                .forEach(System.out::println);
    }

    private static void findMaxOccurredCharacterUsingStreams(String testString){
        String stringWithoutSpaces = testString.replace(" ", "").trim();

        Map<Character, Long> mapWithCharCount = IntStream.range(0, stringWithoutSpaces.length()).mapToObj(index -> stringWithoutSpaces.toCharArray()[index])
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()));

        mapWithCharCount
                .entrySet().stream()
                .filter(characterLongEntry -> characterLongEntry.getValue() == mapWithCharCount.values().stream().sorted(Comparator.reverseOrder()).skip(1).findFirst().get())
                .forEach(System.out::println);
    }

    public static void main(String[] args) {
        findMinOccurrenceOfCharacter("aa bb cc dd e h fff");
        System.out.println(" NEXT OPERATION ");
        findMinOccurredCharacterUsingStreams("aa bb cc dd e h fff");
        findMinOccurredCharacterUsingStreams("aa bb cc dd e h mmmm fff lll");
        System.out.println(" NEXT OPERATION ");
        findMaxOccurredCharacterUsingStreams("aa bb cc dd e h mmmm fff lll");

    }
}
