package com.example.interview_practice;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.example.stream_questions.MultiplyAlternativeNumbersInArray.multipleAlternativeNumbersInArray;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiplyAlternativeNumbersInArrayTest {

    @ParameterizedTest
    @MethodSource("testSource")
    void testCases(int[] arrayOfNumbers, int expectedResult){
        assertEquals(expectedResult, multipleAlternativeNumbersInArray(arrayOfNumbers));
    }

    static Stream testSource(){
        return Stream.of(
                Arguments.of(new int[]{4, 5, 1, 7, 2, 9, 2}, 16),
                Arguments.of(new int[]{1, 2, 1, 3, 1, 4, 1}, 1),
                Arguments.of(new int[]{0, 5, 3, 7}, 0),
                Arguments.of(new int[]{-2, 3, -4, 5}, 8),
                Arguments.of(new int[]{5}, 5)
        );
    }

}
