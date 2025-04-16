package com.example.interview_practice;

import org.junit.jupiter.api.Test;

import static com.example.stream_questions.FindFirstRepeatedChar.findFirstRepeatedCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FindFirstRepeatedCharTest {

    @Test
    void testCases(){
        assertEquals('a', findFirstRepeatedCharacter("abba"));
        assertEquals('s', findFirstRepeatedCharacter("stress"));
        assertNull(findFirstRepeatedCharacter("abcd"));
    }
}
