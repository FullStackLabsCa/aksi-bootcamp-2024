package com.example.interview_practice;

import lombok.Data;

import java.util.List;
import java.util.Map;

public class MapInsideMapQuestion {

    @Data
    private class Student{
        int studentId;
        String studentName;
        List<Course> courseList;
    }

    @Data
    private class Course{
        int courseId;
        String courseName;
    }

    public void listAllCourses(){
        Map<Integer, Student> students = Map.of();

        // for all values in student
        // for all the courses in listOfCourses
        // print the courseName alongwith Student id and studentName

//        students.values().stream().forEach(student ->
//                student.getCourseList().stream().forEach(
//                        course -> System.out.println(course.getCourseName())));
//
//        students.values().stream().forEach(student ->
//                student.getCourseList().stream().distinct().forEach(
//                        course -> System.out.println(course.getCourseName())));
//
//        students.values().stream()
//                .flatMap(student -> student.getCourseList().stream())
//                .forEach(course -> System.out.println(course.getCourseName()));

    }
}
