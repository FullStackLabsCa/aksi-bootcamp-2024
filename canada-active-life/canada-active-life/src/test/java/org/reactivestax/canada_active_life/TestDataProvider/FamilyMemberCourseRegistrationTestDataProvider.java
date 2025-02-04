package org.reactivestax.canada_active_life.TestDataProvider;

import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;

import java.util.function.Supplier;

public interface FamilyMemberCourseRegistrationTestDataProvider {

    Supplier<FamilyMemberCourseRegistrationDTO> validRegistrationDTO = () ->
            FamilyMemberCourseRegistrationDTO.builder()
                    .familyMemberLoginId("anyMember")
                    .offeredCourseId(1)
                    .build();
}
