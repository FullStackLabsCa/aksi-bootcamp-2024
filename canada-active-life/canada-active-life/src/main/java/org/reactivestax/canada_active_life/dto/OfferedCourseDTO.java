package org.reactivestax.canada_active_life.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestax.canada_active_life.validation.CreateOfferedCourse;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferedCourseDTO {

    private int id;

    @NotNull(groups = CreateOfferedCourse.class)
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(groups = CreateOfferedCourse.class)
    private int numOfClassesOffered;

    @NotNull(groups = CreateOfferedCourse.class)
    private int seatsAvailable;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean isAllDayCourse;

    private LocalDate registrationStartDate;

    private String availableForEnrollment;

    @NotNull(groups = CreateOfferedCourse.class)
    private int courseId;

    @NotNull(groups = CreateOfferedCourse.class)
    private int facilityId;
}
