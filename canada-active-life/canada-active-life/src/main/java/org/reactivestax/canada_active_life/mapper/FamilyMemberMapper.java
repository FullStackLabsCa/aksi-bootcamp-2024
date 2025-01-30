package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {
    FamilyMemberDTO toDto(FamilyMember familyMember);

    FamilyMember toEntity(FamilyMemberDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFamilyMemberFromDto(FamilyMemberDTO dto, @MappingTarget FamilyMember entity);

}
