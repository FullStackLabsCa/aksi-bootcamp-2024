package org.reactivestax.canada_active_life.controller;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyMemberCourseRegistrationTestDataProvider;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FamilyManagementController.class)
class FamilyManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @Test
    void testAddFamilyMemberToExistingGroup_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/members";
        String expectedOutcome = "Family Member added to group.";
        String familyMemberDtoJson = FamilyManagementTestDataProvider.goodFamilyMemberDTOJson.get();

        when(familyManagementService.addFamilyMember(any(FamilyMemberDTO.class), any(String.class)))
                .thenReturn(true);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberDtoJson)
                        .header("x-security-header", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testAddFamilyMemberToExistingGroup_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/members";
        String expectedOutcome = "Family Member could not be added to the group.";
        String familyMemberDtoJson = FamilyManagementTestDataProvider.goodFamilyMemberDTOJson.get();

        when(familyManagementService.addFamilyMember(any(FamilyMemberDTO.class), any(String.class)))
                .thenReturn(false);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberDtoJson)
                        .header("x-security-header", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testUpdateFamilyMember_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/members";
        String familyMemberDtoJson = FamilyManagementTestDataProvider.goodFamilyMemberDTOJson.get();

        when(familyManagementService.updateFamilyMemberInfo(any(FamilyMemberDTO.class), any(String.class)))
                .thenReturn(null);

        mockMvc.perform(patch(uriTemplate)
                        .content(familyMemberDtoJson)
                        .param("memberLoginId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeactivateFamilyMember_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/members";
        String expectedOutcome = "Family Member Deactivated";

        when(familyManagementService.deactivateFamilyMember(any(String.class), any(String.class)))
                .thenReturn(true);

        mockMvc.perform(delete(uriTemplate)
                        .param("memberLoginId", "1")
                        .header("x-security-header", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testDeactivateFamilyMember_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/members";
        String expectedOutcome = "Family Member could not be deactivated.";

        when(familyManagementService.deactivateFamilyMember(any(String.class), any(String.class)))
                .thenReturn(false);

        mockMvc.perform(delete(uriTemplate)
                        .param("memberLoginId", "1")
                        .header("x-security-header", UUID.randomUUID()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedOutcome));
    }

}
