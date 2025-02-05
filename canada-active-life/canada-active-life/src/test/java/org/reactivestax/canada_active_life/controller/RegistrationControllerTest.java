package org.reactivestax.canada_active_life.controller;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyMemberCourseRegistrationTestDataProvider;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.service.RegistrationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void testGetAllCourseEnrollmentsForMember_Successful(){}

    @Test
    void testGetAllCourseEnrollmentsForMember_Unsuccessful(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_Successful(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_Unsuccessful(){}

    @Test
    void testGetAllCourseWaitlistForMember_Successful(){}

    @Test
    void testGetAllCourseWaitlistForMember_Unsuccessful(){}
}
