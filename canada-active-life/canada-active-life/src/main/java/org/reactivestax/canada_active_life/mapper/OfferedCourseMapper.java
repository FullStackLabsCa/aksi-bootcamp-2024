package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;

@Mapper(componentModel = "spring")
public interface OfferedCourseMapper {
    OfferedCourseDTO toDto(OfferedCourse offeredCourse);

    OfferedCourse toEntity(OfferedCourseDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOfferedCourseFromDto(OfferedCourseDTO dto, @MappingTarget OfferedCourse entity);

}
