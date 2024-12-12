package org.example;

import java.util.Objects;
import java.util.Scanner;

public class FileReader {

    public void readFile(String filePath) {
        try (Scanner fileReader = new Scanner(Objects.requireNonNull(FileReader.class.getClassLoader().getResourceAsStream(filePath)))) {

            while (fileReader.hasNextLine()) {
                System.out.println(fileReader.nextLine());
            }
        }
    }
}
