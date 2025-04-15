package com.example.interview_practice;

import org.junit.jupiter.api.Test;

import static com.example.stream_questions.FindSumOfUniqueElements.findSumOfUniqueElements;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindSumOfUniqueElementsTest {

    @Test
    void runTestCases(){
        assertEquals(22, findSumOfUniqueElements(new int[]{1, 6, 7, 8, 1, 1, 8, 8, 7}));
        assertEquals(9, findSumOfUniqueElements(new int[]{2, 3, 4}));
        // All Duplicate Elements
        assertEquals(5, findSumOfUniqueElements(new int[]{5, 5, 5}));
        // Empty Array
        assertEquals(0, findSumOfUniqueElements(new int[]{}));
        // Mixed Duplicates
        assertEquals(6, findSumOfUniqueElements(new int[]{1, 2, 2, 3}));
        // Negative and Zero Values
        assertEquals(2, findSumOfUniqueElements(new int[]{-1, 0, 3, -1}));
        // Single Element
        assertEquals(10, findSumOfUniqueElements(new int[]{10}));
        // Multiple Duplicates with Varied Values
        assertEquals(15, findSumOfUniqueElements(new int[]{4, 5, 4, 5, 6}));
    }
}
