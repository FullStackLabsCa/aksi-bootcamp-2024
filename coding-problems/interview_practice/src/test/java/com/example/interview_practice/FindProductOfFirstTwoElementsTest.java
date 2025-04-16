package com.example.interview_practice;

import org.junit.jupiter.api.Test;

import static com.example.stream_questions.FindProductOfFirstTwoElements.findProductOfFirstTwoElements;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindProductOfFirstTwoElementsTest {

    @Test
    void testCases(){
        assertEquals(60, findProductOfFirstTwoElements(new int[]{12, 5, 6, 9, 2, 4}));
    }

    @Test
    public void testValidProduct() {
        assertEquals(15, findProductOfFirstTwoElements(new int[]{3, 5}));
        assertEquals(-12, findProductOfFirstTwoElements(new int[]{-3, 4}));
        assertEquals(0, findProductOfFirstTwoElements(new int[]{0, 5}));
    }
}
