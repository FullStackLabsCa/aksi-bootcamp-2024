package org.reactivestax.canada_active_life.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FamilyMemberCourseRegistrationDTO {

    private int id;

    private double cost;

    private LocalDate enrollmentDate;

    private boolean isWithdrawn;

    private double withdrawnCredits;

    private String enrollmentActor;

    private int enrollmentActorId;

    private int offeredCourseId;

    private String familyMemberLoginId;
}
