package com.example.stream_questions;

import java.util.HashSet;
import java.util.stream.IntStream;

public class RemoveDuplicates {
    // Write a program to remove the duplicates.
    //Don’t count blank spaces

    /*
    The the quick brown fox jumped over the lazy dog. It it didn't see that that the dog was actually awake. The fox fox looked back back, surprised surprised by the sudden movement. It was was a strange strange morning in the the forest.
     */

    private static void removeDuplicatesFromString(String testString){
        String[] words = testString.trim().split(" ");

        String lastWord = "";
        for(int i = 0; i < words.length; i++){
            if(words[i]
                .toLowerCase()
                .replace(",", "")
                .replace(".", "")
                    .equals(lastWord
                            .toLowerCase()
                            .replace(",", "")
                            .replace(".", ""))) {
                lastWord = words[i];
                words[i] = "";
            }
            else lastWord = words[i];
        }

        String resultingString = "";
        for(String word : words){
            if(!word.isEmpty())
                resultingString = resultingString + word + " ";
        }

        System.out.println(resultingString);
    }

    private static void removeDuplicatesFromStringUsingStreams(String testString){

    }

    public static void main(String[] args) {
        removeDuplicatesFromString("    The the quick brown fox jumped over the lazy dog. It it didn't see that that the dog was actually awake. The fox fox looked back back, surprised surprised by the sudden movement. It was was a strange strange morning in the the forest.");
    }
}
