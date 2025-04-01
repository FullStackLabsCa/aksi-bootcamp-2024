package com.example.stream_questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SplitEvenOddLists {

    private static ArrayList<ArrayList<Integer>> splitListIntoOddAndEvenLists(int[] integerArray){
        ArrayList<Integer> evenInt = Arrays.stream(integerArray)
                .boxed()
                .filter(number -> number % 2 == 0)
                .collect(Collectors.toCollection(ArrayList::new));


        ArrayList<Integer> oddInt = Arrays.stream(integerArray)
                .boxed()
                .filter(number -> number % 2 == 1)
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        result.add(evenInt);
        result.add(oddInt);

        return result;
    }

    private static void splitListIntoOddAndEvenListBetter(int[] integerArray){
        List<List<Integer>> result = Arrays.stream(integerArray)
                .boxed()
                .collect(Collectors.groupingBy(x -> x % 2 == 0))
                .entrySet()
                .stream()
                .map(x -> x.getValue())
                .toList();

        System.out.println(result);
    }

    public static void main(String[] args) {
        splitListIntoOddAndEvenLists(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        splitListIntoOddAndEvenListBetter(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
    }
}
