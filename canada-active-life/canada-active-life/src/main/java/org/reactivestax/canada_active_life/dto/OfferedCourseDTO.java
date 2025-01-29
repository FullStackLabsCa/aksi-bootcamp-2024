package org.reactivestax.canada_active_life.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OfferedCourseDTO {

    private int id;

    private LocalDate startDate;

    private LocalDate endDate;

    private int numOfClassesOffered;

    private int seatsAvailable;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean isAllDayCourse;

    private LocalDate registrationStartDate;

    private String availableForEnrollment;
}
