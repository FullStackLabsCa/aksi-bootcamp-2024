package com.example.stream_questions;

import java.util.stream.IntStream;

public class MultiplyAlternativeNumbersInArray {

    public static int multipleAlternativeNumbersInArray(int[] numbers){
        int product = 1;
        return IntStream.range(0, numbers.length)
                .filter(index -> index % 2 == 0)
                .map(index -> product * numbers[index])
                .reduce(1, (a, b) -> a * b);
    }

    public static void main(String[] args) {
        multipleAlternativeNumbersInArray(new int[]{4, 5, 1, 7, 2, 9, 2});
    }
}
