package com.example.stream_questions;

import org.hibernate.Hibernate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RearrangeNumbersHighestLowestValues {

    private static void rearrangeNumbersToFormHighestAndLowestPossibleValue(int[] numbers){
        Arrays.stream(numbers)
                .boxed()
                .sorted()
                .forEach(System.out::print);
        System.out.println();
        Arrays.stream(numbers)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .forEach(System.out::print);

    }

    public static void main(String[] args) {
        rearrangeNumbersToFormHighestAndLowestPossibleValue(new int[]{1, 2,3, 4, 5});
    }
}
