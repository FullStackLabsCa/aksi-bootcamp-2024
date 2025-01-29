package org.reactivestax.canada_active_life.dto;

import lombok.Data;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.OfferedCourse;

import java.time.LocalDate;

@Data
public class CourseMemberRegistrationDTO {

    private int id;

    private double cost;

    private LocalDate enrollmentDate;

    private boolean isWithdrawn;

    private double withdrawnCredits;

    private String enrollmentActor;

    private int enrollmentActorId;

    private OfferedCourse offeredCourseId;

    private FamilyMember familyMemberId;
}
