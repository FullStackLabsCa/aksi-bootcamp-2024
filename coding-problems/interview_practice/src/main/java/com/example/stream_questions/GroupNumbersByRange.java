package com.example.stream_questions;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GroupNumbersByRange {

    public static void groupNumbersByRange(int[] numbers){
        Arrays.stream(numbers)
                .boxed()
                .collect(Collectors.groupingBy(x -> x / 10))
                .entrySet().stream()
                .forEach(integerListEntry -> System.out.println(integerListEntry.getKey() + "=" + integerListEntry.getValue().toString()));
    }

    public static void main(String[] args) {
        groupNumbersByRange(new int[]{2, 3, 10, 14, 20, 24, 30, 34, 40, 44, 50, 54});
    }
}
