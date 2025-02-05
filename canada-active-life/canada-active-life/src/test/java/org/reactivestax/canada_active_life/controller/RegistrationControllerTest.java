package org.reactivestax.canada_active_life.controller;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyMemberCourseRegistrationTestDataProvider;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseWaitlistDTO;
import org.reactivestax.canada_active_life.service.RegistrationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationManagementService registrationManagementService;

    @Test
    void testEnrollFamilyMemberInAnOfferedCourse_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations/enrollment";
        String expectedOutcome = "Family Member Enrolled in the Offered Course.";
        String familyMemberCourseRegistrationDTO = FamilyMemberCourseRegistrationTestDataProvider.validRegistrationDTOJson.get();

        when(registrationManagementService.enrollFamilyMemberInOfferedCourse(any(FamilyMemberCourseRegistrationDTO.class), any(String.class)))
                .thenReturn(true);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberCourseRegistrationDTO)
                        .header("x-security-header", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testEnrollFamilyMemberInAnOfferedCourse_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations/enrollment";
        String expectedOutcome = "Failed to enroll the Family Member in the desired offered course...";
        String familyMemberCourseRegistrationDTO = FamilyMemberCourseRegistrationTestDataProvider.validRegistrationDTOJson.get();

        when(registrationManagementService.enrollFamilyMemberInOfferedCourse(any(FamilyMemberCourseRegistrationDTO.class), any(String.class)))
                .thenReturn(false);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberCourseRegistrationDTO)
                        .header("x-security-header", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testGetAllCourseEnrollmentsForMember_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations/enrollment";

        ArrayList<FamilyMemberCourseRegistrationDTO> registrationDTOS = new ArrayList<>();
        registrationDTOS.add(FamilyMemberCourseRegistrationDTO.builder().build());
        when(registrationManagementService.getEnrollmentsForMember(any(String.class)))
                .thenReturn(registrationDTOS);

        mockMvc.perform(get(uriTemplate)
                        .param("memberLoginId", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllCourseEnrollmentsForMember_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations/enrollment";

        when(registrationManagementService.getEnrollmentsForMember(any(String.class)))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get(uriTemplate)
                        .param("memberLoginId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations";
        String expectedOutcome = "Family Member withdrawn from the Offered Course.";

        when(registrationManagementService.withdrawFamilyMemberFromOfferedCourse(any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(true);

        mockMvc.perform(delete(uriTemplate)
                        .param("offeredCourseId", "1")
                        .param("memberLoginId", UUID.randomUUID().toString())
                        .header("x-security-header", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations";
        String expectedOutcome = "Failed to withdraw the Family Member from the offered course...";

        when(registrationManagementService.withdrawFamilyMemberFromOfferedCourse(any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(false);

        mockMvc.perform(delete(uriTemplate)
                        .param("offeredCourseId", "1")
                        .param("memberLoginId", UUID.randomUUID().toString())
                        .header("x-security-header", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testGetAllCourseWaitlistForMember_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations/waitlist";

        ArrayList<FamilyMemberCourseWaitlistDTO> waitlistDTOS = new ArrayList<>();
        waitlistDTOS.add(FamilyMemberCourseWaitlistDTO.builder().build());
        when(registrationManagementService.getWaitlistForMember(any(String.class)))
                .thenReturn(waitlistDTOS);

        mockMvc.perform(get(uriTemplate)
                        .param("memberLoginId", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllCourseWaitlistForMember_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/courseRegistrations/waitlist";

        when(registrationManagementService.getWaitlistForMember(any(String.class)))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get(uriTemplate)
                        .param("memberLoginId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }
}
