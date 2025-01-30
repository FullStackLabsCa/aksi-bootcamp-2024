package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.Mapper;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {
    FamilyMemberDTO toDto(FamilyMember familyMember);

    FamilyMember toEntity(FamilyMemberDTO dto);

}
