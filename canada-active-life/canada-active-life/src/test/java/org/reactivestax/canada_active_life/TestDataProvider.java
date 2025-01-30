package org.reactivestax.canada_active_life;

import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;

import java.util.function.Supplier;

public interface TestDataProvider {
    Supplier<FamilyMemberDTO> goodFamilyMemberDTO = () ->
            FamilyMemberDTO.builder()
                    .build();
}
