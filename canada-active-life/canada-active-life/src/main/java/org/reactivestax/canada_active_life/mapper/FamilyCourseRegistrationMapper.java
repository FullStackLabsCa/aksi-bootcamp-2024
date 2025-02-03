package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.Mapper;
import org.reactivestax.canada_active_life.domain.FamilyCourseRegistration;
import org.reactivestax.canada_active_life.dto.CourseMemberRegistrationDTO;

@Mapper(componentModel = "spring")
public interface FamilyCourseRegistrationMapper {
    CourseMemberRegistrationDTO toDto(FamilyCourseRegistration familyCourseRegistration);
    FamilyCourseRegistration toEntity(CourseMemberRegistrationDTO dto);
}
