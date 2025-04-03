//package com.example.stack_overflow;
//
//import groovy.lang.Binding;
//import groovy.lang.GroovyShell;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ExecuteDoubleInitializations {
//    public static void main(String[] args) {
//        // File path to the initialization file
//        String filePath = "src/main/resources/double_initializations.txt";
//
//        // Map to store the initialized double variables
//        Map<String, Double> doubleVariables = new HashMap<>();
//
//        // Groovy binding to share variables between Java and Groovy
//        Binding binding = new Binding();
//        GroovyShell shell = new GroovyShell(binding);
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Execute the line as a Groovy script
//                shell.evaluate(line);
//
//                // Extract the variable name and value from the binding
//                String varName = line.split(" ")[1]; // Extract variable name (e.g., "var1")
//                double varValue = (double) binding.getVariable(varName); // Get the value
//
//                // Store the variable in the map
//                doubleVariables.put(varName, varValue);
//            }
//
//            // Print all initialized variables
//            System.out.println("Initialized double variables:");
//            doubleVariables.forEach((name, value) -> System.out.println(name + " = " + value));
//
//        } catch (IOException e) {
//            System.err.println("Error reading the file: " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("Error executing the script: " + e.getMessage());
//        }
//    }
//}