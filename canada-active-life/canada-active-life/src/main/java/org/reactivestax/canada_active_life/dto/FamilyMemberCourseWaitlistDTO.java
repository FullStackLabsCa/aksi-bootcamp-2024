package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.OfferedCourse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberCourseWaitlistDTO {
    private int id;

    private String enrollmentActor;

    private int enrollmentActorId;

    private OfferedCourse offeredCourse;

    private FamilyMember familyMember;
}
