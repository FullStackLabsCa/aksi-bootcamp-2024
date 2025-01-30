package org.reactivestax.canada_active_life;

import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

public interface TestDataProvider {
    Supplier<FamilyMemberDTO> goodFamilyMemberDTO = () ->
            FamilyMemberDTO.builder()
                    .name("Jane Doe")  // Setting string value for 'name'
                    .dob(LocalDate.parse("1992-08-15"))  // Parsing string to LocalDate for 'dob'
                    .gender("Female")
                    .emailAddress("jane.doe@example.com")
                    .streetNumber("456")
                    .streetName("Elm Street")
                    .city("Springfield")
                    .province("Illinois")
                    .country("USA")
                    .homePhoneNumber("555-123-4567")
                    .businessPhoneNumber("555-987-6543")
                    .preferredContactMethod("Email")
                    .language("English")
                    .memberLoginId(UUID.randomUUID().toString())
                    .familyPin("FAM123456")
                    .build();

    Supplier<FamilyMemberDTO> goodFamilyMemberDTOForPatch = () ->
            FamilyMemberDTO.builder()
                    .province("Karnal")
                    .country("Uganda")
                    .build();

    Supplier<UserLoginDTO> goodLoginDTO = () ->
            UserLoginDTO.builder()
                    .memberLoginId("121222")
                    .familyPin("FAM123456")
                    .build();
}
