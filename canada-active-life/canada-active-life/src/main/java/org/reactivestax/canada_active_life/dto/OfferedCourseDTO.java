package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
