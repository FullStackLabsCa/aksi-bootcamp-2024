package org.example;

import java.util.Objects;
import java.util.Scanner;

public class FileReader {

    public void readFile(String filePath) {
        try (Scanner fileReader = new Scanner(Objects.requireNonNull(FileReader.class.getClassLoader().getResourceAsStream(filePath)))) {

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String key = line.substring(0,2);
                String value = line.substring(2);
                System.out.println("key = " + key);
                System.out.println("value = " + value);
            }
        }
    }
}
