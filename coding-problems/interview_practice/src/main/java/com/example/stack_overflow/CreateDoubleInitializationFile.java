package com.example.stack_overflow;

import java.io.FileWriter;
import java.io.IOException;

public class CreateDoubleInitializationFile {
    public static void main(String[] args) {
        // File path where the text file will be created
        String filePath = "double_initializations.txt";

        // Number of double variables to initialize
        int numberOfDoubles = 30000;

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write 30,000 double initializations to the file
            for (int i = 1; i <= numberOfDoubles; i++) {
                double value = i * 1.0; // Initialize double variable with a value
                String line = "double var" + i + " = " + value + ";"; // Create initialization line
                writer.write(line); // Write the line to the file
                writer.write(System.lineSeparator()); // Add a new line after each initialization
            }

            System.out.println("File created successfully with 30,000 double initializations.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}