package com.example.stream_questions;

import java.util.stream.IntStream;

public class MultiplyFirstAndLastElementInArray {

    public static int multiplyFirstAndLastElementInArray(int[] numbers) {

        IntStream.range(0, numbers.length % 2 == 0 ? (numbers.length / 2) : (numbers.length) / 2 + 1)
                .map(index -> numbers[index] * numbers[numbers.length - index - 1])
                .forEach(System.out::println);
        System.out.println();
        return -1;
    }

    public static void main(String[] args) {
        multiplyFirstAndLastElementInArray(new int[]{4, 5, 1, 7, 2, 9});
        multiplyFirstAndLastElementInArray(new int[]{1, 2, 3, 4});
        multiplyFirstAndLastElementInArray(new int[]{5, 2, 3});
        multiplyFirstAndLastElementInArray(new int[]{-1, -2, -3});
    }
}
