package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.Mapper;
import org.reactivestax.canada_active_life.domain.FamilyCourseRegistration;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;

@Mapper(componentModel = "spring")
public interface FamilyCourseRegistrationMapper {
    FamilyMemberCourseRegistrationDTO toDto(FamilyCourseRegistration familyCourseRegistration);
    FamilyCourseRegistration toEntity(FamilyMemberCourseRegistrationDTO dto);
}
