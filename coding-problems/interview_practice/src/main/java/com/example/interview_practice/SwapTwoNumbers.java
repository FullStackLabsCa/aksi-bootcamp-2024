package com.example.interview_practice;

public class SwapTwoNumbers {

    private static void swapTwoNumber(double a, double b){
        if(a==0){
            a=b;
            b=0;
        } else if(b ==0){
            b=a;
            a=0;
        } else {
            a = b / a;
            b = b / a;
            a = a * b;
        }

        System.out.println("a = " + a + "; and b = " + b);
    }

    public static void main(String[] args) {
        swapTwoNumber(3.14,6.28);
    }
}
