package com.example.interview_practice;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Transient
    private transient String firstName;
    private String lastName;
}
