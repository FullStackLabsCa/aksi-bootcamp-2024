package org.reactivestax.canada_active_life;

import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.dto.CustomerDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;
import org.reactivestax.canada_active_life.dto.UserVerificationDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FamilyManagementTestDataProvider {
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

    Supplier<FamilyMember> goodFamilyMember = () ->
        FamilyMember.builder()
                .familyGroup(FamilyGroup.builder()
                        .status("inactive")
                        .build())
                .build();

    Supplier<FamilyGroup> goodFamilyGroup = () ->
            FamilyGroup.builder()
                    .status("inactive")
                    .build();

    Supplier<FamilyMemberDTO> invalidFamilyMemberDTO = () ->
            FamilyMemberDTO.builder()
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

    Supplier<UserVerificationDTO> goodLoginVerificationDTO = () ->
            UserVerificationDTO.builder()
                    .otpEnteredByUser("FAM123456")
                    .build();
}
