package com.example.stream_questions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FindUniqueInArray {


    private static int findUnique(int[] integerArray){
        HashSet<Integer> hashSet = new HashSet<>();

        for(int i=0; i < integerArray.length; i++){
            boolean foundDuplicate = false;
            for(int j = 0; j < integerArray.length; j++){
                if(integerArray[i] == integerArray[j] && i!=j)
                    foundDuplicate = true;
            }
            if(!foundDuplicate)
                return integerArray[i];
        }
        return 0;
    }

    private static void findUniqueUsingStream(int[] integerArray){
        Arrays.stream(integerArray)
                .boxed()
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(key -> key.getValue() == 1L)
                .forEach(System.out::println);
    }

    public static void main(String[] args) {
        int[] integerArray = {0, 1, 0, 1, 2};
        System.out.println(findUnique(integerArray));
        findUniqueUsingStream(integerArray);
    }
}
