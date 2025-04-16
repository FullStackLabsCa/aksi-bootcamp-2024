package com.example.interview_practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.stream_questions.IntListFromStringArray.intFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntListFromStringArrayTest {

    @Test
    void testCases(){
        assertEquals(Arrays.asList(123, 456), intFromString(new String[]{"abc", "123", "456", "xyz"}));
    }

    @Test
    public void testValidIntegers() {
        List<Integer> expected = Arrays.asList(123, -456, 0);
        assertEquals(expected, intFromString(new String[]{"123", "-456", "0"}));
    }

    @Test
    public void testLeadingZeros() {
        List<Integer> expected = Arrays.asList(7, -1);
        assertEquals(expected, intFromString(new String[]{"007", "-0001"}));
    }

    @Test
    public void testBoundaryValues() {
        List<Integer> expected = Arrays.asList(2147483647, -2147483648);
        assertEquals(expected, intFromString(new String[]{"2147483647", "-2147483648"}));
    }

    @Test
    public void testNonNumericStrings() {
        List<Integer> expected = Arrays.asList();
        assertEquals(expected, intFromString(new String[]{"abc", "12.34", "1,234"}));
    }

    @Test
    public void testOverflowValues() {
        List<Integer> expected = Arrays.asList();
        assertEquals(expected, intFromString(new String[]{"2147483648", "-2147483649"}));
    }

    @Test
    public void testEmptyList() {
        List<Integer> expected = Arrays.asList();
        assertEquals(expected, intFromString(new String[]{}));
    }

    @Test
    public void testNullEntries() {
        List<String> input = Arrays.asList(null, "123");
        List<Integer> expected = Arrays.asList(123);
        assertEquals(expected, intFromString(new String[]{null, "123"}));
    }
}
