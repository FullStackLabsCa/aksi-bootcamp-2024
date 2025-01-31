package org.reactivestax.canada_active_life;

import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;
import org.reactivestax.canada_active_life.dto.UserVerificationDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

public interface OfferedCourseTestDataProvider {
    Supplier<OfferedCourseDTO> goodOfferedCourseDTO = () ->
            OfferedCourseDTO.builder()
                .startDate(LocalDate.of(2025, 1, 15))
                .endDate(LocalDate.of(2025, 3, 15))
                .numOfClassesOffered(10)
                .seatsAvailable(25)
                .startTime(LocalDateTime.of(2025, 1, 15, 9, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 11, 0))
                .isAllDayCourse(false)
                .registrationStartDate(LocalDate.of(2025, 1, 1))
                .availableForEnrollment("OPEN")
                .courseId(1)
                .facilityId(2)
                .build();

    Supplier<OfferedCourseDTO> goodOfferedCourseDTOForPatch = () ->
            OfferedCourseDTO.builder()
                    .seatsAvailable(25)
                    .isAllDayCourse(false)
                    .build();

}
