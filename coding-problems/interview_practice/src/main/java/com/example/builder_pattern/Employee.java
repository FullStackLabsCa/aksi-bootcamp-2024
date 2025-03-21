package com.example.builder_pattern;

import lombok.Data;

@Data
public class Employee {

    private String name;
    private String lastName;
    private int age;

    private Employee() {
    }

    private Employee(EmployeeBuilder builder){
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.age = builder.age;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                '}';
    }

    public static EmployeeBuilder builder(){
        return new EmployeeBuilder();
    }

    public static class EmployeeBuilder {
        private String name;
        private String lastName;
        private int age;

        public EmployeeBuilder firstName(String name) {
            this.name = name;
            return this;
        }

        public EmployeeBuilder lastName(String lastName){
            this.lastName = lastName;
            return this;
        }

        public EmployeeBuilder age(int age) {
            this.age = age;
            return this;
        }

        public Employee build(){
            return new Employee(this);
        }
    }
}
