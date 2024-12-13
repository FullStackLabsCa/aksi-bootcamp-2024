package org.example;

import java.time.Duration;
import java.time.LocalDateTime;

public class App
{
    public static void main( String[] args )
    {
        LocalDateTime startTime = LocalDateTime.now();
        FileReader fileReader = new FileReader();
        fileReader.readFile("bony_sal_ruleset_d050514.data");

        Duration duration = Duration.between(startTime, LocalDateTime.now());
        System.out.println("duration = " + duration.getSeconds());

    }
}
