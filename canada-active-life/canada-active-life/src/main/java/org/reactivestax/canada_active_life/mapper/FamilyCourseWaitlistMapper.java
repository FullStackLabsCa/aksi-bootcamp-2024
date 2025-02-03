package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.Mapper;
import org.reactivestax.canada_active_life.domain.FamilyCourseWaitlist;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseWaitlistDTO;

@Mapper(componentModel = "spring")
public interface FamilyCourseWaitlistMapper {
    FamilyMemberCourseWaitlistDTO toDto(FamilyCourseWaitlist familyCourseWaitlist);
    FamilyCourseWaitlist toEntity(FamilyMemberCourseWaitlistDTO dto);
}
