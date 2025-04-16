package com.example.stream_questions;

import java.util.Arrays;

public class FindProductOfFirstTwoElements {
    public static int findProductOfFirstTwoElements(int[] numbers){
        return Arrays.stream(numbers)
                .boxed()
                .limit(2)
                .reduce(1, (a, b) -> a * b);
    }

    public static void main(String[] args) {
        System.out.println(findProductOfFirstTwoElements(new int[]{12, 5, 6, 9, 2, 4}));
    }
}
