package org.reactivestax.canada_active_life.dto;

import lombok.Data;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.OfferedCourse;

@Data
public class CourseMemberWaitlistDTO {
    private int id;

    private String enrollmentActor;

    private int enrollmentActorId;

    private OfferedCourse offeredCourse;

    private FamilyMember familyMember;
}
