package com.example.stream_questions;

import java.util.Arrays;

public class GroupAnagrams {

    public static void groupAnagrams(String[] strings){
        Arrays.stream(strings)

    }

    public static void main(String[] args) {
        groupAnagrams(new String[]{"pat", "tap", "pan", "nap", "Team", "tree", "meat"});
        // Output Expected: [[pan, nap], [pat, tap], [Team, meat], [tree]]
    }
}
