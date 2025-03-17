package com.example.interview_practice;

import java.util.stream.IntStream;

public class SumPrimeNumbers {

    private static boolean isNumberPrime(int number){
        int factors = 0;

        for(int i = 1; i <= number; i++){
            if(number%i == 0)
                factors++;
        }

        if(factors == 2) return true;
        return false;
    }

    private static int sumOfPrimeNumbers(int n){
        return IntStream.rangeClosed(1, n).filter(SumPrimeNumbers::isNumberPrime).sum();
    }

    public static void main(String[] args) {
        System.out.println(sumOfPrimeNumbers(1000));
    }
}
