package com.example.stream_questions;

import java.util.Arrays;
import java.util.List;

public class IntListFromStringArray {

    public static List<Integer> intFromString(String[] strings){
        return Arrays.stream(strings)
                .map(element -> {
                    try {
                        return Integer.valueOf(element);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(element -> element != null)
                .toList();
    }

    public static void main(String[] args) {
        System.out.println(intFromString(new String[]{"abc", "123", "456", "xyz"}));
    }
}
