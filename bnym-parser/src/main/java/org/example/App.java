package org.example;

import java.io.File;

public class App
{
    public static void main( String[] args )
    {
        FileReader fileReader = new FileReader();
        fileReader.readFile("bony_cbna_ruleset_d100314.data");
    }
}
