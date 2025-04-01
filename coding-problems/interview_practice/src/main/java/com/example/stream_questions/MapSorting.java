package com.example.stream_questions;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapSorting {

    public static void main(String[] args) {
     // There is a map<String, Integer> sort the map in descending order of values
        HashMap<String, Integer> map = new HashMap<>();

        // Output should be a map of key and Value
        Map<String, Integer> collect = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (key, key2) -> key, LinkedHashMap::new));

        LinkedHashMap<String, Integer> collectw =
                map.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
