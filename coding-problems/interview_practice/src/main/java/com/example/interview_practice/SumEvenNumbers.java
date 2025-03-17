package com.example.interview_practice;

import java.util.stream.IntStream;

public class SumEvenNumbers {

    private static int calculateSumOfEvenNumbers(int n){
        int sum = 0;
        for(int i = 1; i <= n; i++){
            if(i % 2  == 0)
                sum = sum + i;
        }
        return sum;
    }

    private static int calculateSumOfEvenUsingStreams(int n){
        return IntStream.rangeClosed(1, n).filter(i -> i % 2 == 0).sum();
    }

    public static void main(String[] args) {
        System.out.println(calculateSumOfEvenNumbers(50));
        System.out.println(calculateSumOfEvenUsingStreams(50));
    }
}
