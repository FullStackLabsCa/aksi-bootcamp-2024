package com.example.interview_practice;

public class ExpandedForm {

    private static void expandedForm(int number){
        for(int i = number/10; i >= 0 ; i--){
            double mathPow = Math.pow(10, i);

            int a = (int) (number/mathPow);
            if(a != 0) {
                System.out.print(a + " x " + (int) mathPow);
                number = (int) (number % (a * mathPow));
                if (i != 0)
                    System.out.print(" + ");
            }
        }
        System.out.println("");
    }

    public static void main(String[] args) {
        expandedForm(25);
        expandedForm(3913);
        expandedForm(5008);
        expandedForm(7);
        expandedForm(100203);
    }
}
