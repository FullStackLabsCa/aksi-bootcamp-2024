package com.example.stream_questions;

import java.util.Arrays;

public class FindSumOfUniqueElements {

    public static int findSumOfUniqueElements(int[] array){

        int sum = Arrays.stream(array)
                .distinct()
                .sum();

        return sum;
    }

    public static void main(String[] args) {
        findSumOfUniqueElements(new int[]{1, 6, 7, 8, 1, 1, 8, 8, 7});
    }
}
