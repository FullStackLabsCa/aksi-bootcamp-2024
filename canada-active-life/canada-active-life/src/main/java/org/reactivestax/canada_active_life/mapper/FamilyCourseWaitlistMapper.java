package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.Mapper;
import org.reactivestax.canada_active_life.domain.FamilyCourseWaitlist;
import org.reactivestax.canada_active_life.dto.CourseMemberWaitlistDTO;

@Mapper(componentModel = "spring")
public interface FamilyCourseWaitlistMapper {
    CourseMemberWaitlistDTO toDto(FamilyCourseWaitlist familyCourseWaitlist);
    FamilyCourseWaitlist toEntity(CourseMemberWaitlistDTO dto);
}
