package com.example.stream_questions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class MovingZerosToBeginning {

    public static void moveZerosToBeginning(int[] array) {
        AtomicInteger count = new AtomicInteger();
        List<Integer> list = new java.util.ArrayList<>(Arrays.stream(array)
                .boxed()
                .filter(integer -> {
                    if (integer == 0) count.getAndIncrement();
                    return integer != 0;
                })
                .toList());

        for(int i = 0; i < count.get() ; i++)
            list.add(0, 0);

        System.out.println("list = " + list);
    }

    public static int[] moveZerosToBeginningV2(int[] array){
        int[] zeros = Arrays.stream(array).filter(x -> x == 0).toArray();
        int[] notZeros = Arrays.stream(array).filter(x -> x != 0).toArray();

        int[] result = IntStream.concat(Arrays.stream(zeros), Arrays.stream(notZeros)).toArray();
        Arrays.stream(result).forEach(System.out::print);
        System.out.println();
        return result;
    }

    public static void main(String[] args) {
        moveZerosToBeginningV2(new int[]{5, 0, 1, 0, 8, 0});
        moveZerosToBeginningV2(new int[]{5, 1, 8});
        moveZerosToBeginningV2(new int[]{0, 0, 0});
        moveZerosToBeginningV2(new int[]{0, 5, 0, 3, 0});
        // output: {0, 0, 0, 5, 1, 8}
    }
}
