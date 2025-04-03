package com.example.stack_overflow;

import java.io.FileWriter;
import java.io.IOException;

public class CreateDoubleFile {
    public static void main(String[] args) {
        // File path where the text file will be created
        String filePath = "doubles.txt";

        // Number of double variables to write
        int numberOfDoubles = 30000;

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write 30,000 double values to the file
            for (int i = 1; i <= numberOfDoubles; i++) {
                double value = i; // Initialize double variable with sequential value
                writer.write(String.valueOf(value)); // Write the double value to the file
                writer.write(System.lineSeparator()); // Add a new line after each value
            }

            System.out.println("File created successfully with 30,000 double values.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}