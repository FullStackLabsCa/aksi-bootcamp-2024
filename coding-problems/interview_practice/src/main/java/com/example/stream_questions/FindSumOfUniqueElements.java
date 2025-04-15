package com.example.stream_questions;

import java.util.Arrays;

public class FindSumOfUniqueElements {

    public static int findSumOfUniqueElements(int[] array){

        return Arrays.stream(array)
                .distinct()
                .sum();
    }

    public static void main(String[] args) {
        findSumOfUniqueElements(new int[]{1, 6, 7, 8, 1, 1, 8, 8, 7});
    }
}
