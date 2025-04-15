package com.example.interview_practice;

import org.junit.jupiter.api.Test;

import static com.example.stream_questions.FindFirstNonRepeatedChar.findFirstNonRepeatedCharacter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindFirstNonRepeatedCharTest {

    @Test
    void testCases(){
        assertEquals('H', findFirstNonRepeatedCharacter("Hello World"));
    }
    @Test
    public void testFindFirstNonRepeatedCharacter() {
        // Example Case
        assertEquals(Character.valueOf('t'), findFirstNonRepeatedCharacter("stress"));

        // All Unique Characters
        assertEquals(Character.valueOf('a'), findFirstNonRepeatedCharacter("abcd"));

        // All Duplicates
        assertEquals(null, findFirstNonRepeatedCharacter("aabbcc"));

        // Non-Repeated at Start
        assertEquals(Character.valueOf('a'), findFirstNonRepeatedCharacter("abc"));

        // Non-Repeated in Middle
        assertEquals(Character.valueOf('b'), findFirstNonRepeatedCharacter("abac"));

        // Non-Repeated at End
        assertEquals(Character.valueOf('c'), findFirstNonRepeatedCharacter("aabbc"));

        // Case Sensitivity
        assertEquals(Character.valueOf('T'), findFirstNonRepeatedCharacter("sTreSS"));

        // With Space
        assertEquals(Character.valueOf(' '), findFirstNonRepeatedCharacter("a a b"));

        // Single Character
        assertEquals(Character.valueOf('x'), findFirstNonRepeatedCharacter("x"));

        // Numbers and Letters
        assertEquals(Character.valueOf('4'), findFirstNonRepeatedCharacter("1122334"));

        // Empty String
        assertEquals(null, findFirstNonRepeatedCharacter(""));

        // Multiple Spaces
        assertEquals(null, findFirstNonRepeatedCharacter("   "));

        // Case Insensitivity Check (if function is case-sensitive)
        assertEquals(Character.valueOf('S'), findFirstNonRepeatedCharacter("Ss"));

        // Non-Repeated After Duplicates
        assertEquals(Character.valueOf('d'), findFirstNonRepeatedCharacter("aaaabbbccd"));

        // Mixed Symbols
        assertEquals(Character.valueOf('h'), findFirstNonRepeatedCharacter("h!e!l!l!o"));

        // Non-Repeated Followed by Duplicates
        assertEquals(Character.valueOf('x'), findFirstNonRepeatedCharacter("abcabcx"));
    }
}
