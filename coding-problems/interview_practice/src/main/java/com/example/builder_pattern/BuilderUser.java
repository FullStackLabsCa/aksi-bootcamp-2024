package com.example.builder_pattern;

public class BuilderUser {
    public static void main(String[] args) {
        Employee employee =
                Employee.builder()
                        .firstName("Akshat")
                        .lastName("Singla")
                        .age(24)
                        .build();

        System.out.println(employee.toString());
    }
}
