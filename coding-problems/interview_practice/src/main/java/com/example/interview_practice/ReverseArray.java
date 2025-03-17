package com.example.interview_practice;

public class ReverseArray <T> {

    private T[] reverseArray(T[] array){

        int lengthOfArray = array.length;
        T[] reversedArray = (T[]) new Object[lengthOfArray];

        for(int i = 0; i < lengthOfArray; i++){
            reversedArray[i] = array[lengthOfArray-1-i];
        }

        return reversedArray;
    }

    public static void main(String[] args) {
        Integer[] array = {1, 2, 3, 4, 5};
        ReverseArray<Integer> reverseArray = new ReverseArray<>();
        Integer[] reversedArray = reverseArray.reverseArray(array);
        for(Integer item : reversedArray)
            System.out.println(item);
    }
}
