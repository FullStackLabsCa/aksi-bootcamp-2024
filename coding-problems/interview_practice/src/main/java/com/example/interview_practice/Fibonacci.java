package com.example.interview_practice;

public class Fibonacci {

    private static void fibonacciWithoutRecursion(int n) {
        int a = 1;
        int b = 2;

        for (int i = 0; i <= n; i++) {
            b = a + b;
            a = b - a;
            System.out.println(b);
        }
    }

    private static int fibonacciUsingRecursion(int n) {
        if (n <= 1)
            return 1;
        else {
            return fibonacciUsingRecursion(n-2) + fibonacciUsingRecursion(n - 1);
        }
    }

    public static void main(String[] args) {
        fibonacciWithoutRecursion(5);
//        System.out.println(fibonacciUsingRecursion(2));
    }
}
