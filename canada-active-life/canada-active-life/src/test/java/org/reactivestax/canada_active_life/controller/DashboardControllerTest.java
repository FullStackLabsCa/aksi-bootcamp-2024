package org.reactivestax.canada_active_life.controller;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.service.DashboardService;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @Test
    void testSignUpMember_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/signup";
        String expectedOutcome = "Family Member created. Please click on the Activation Link in the email to activate the account.";
        String familyMemberDTOJson = FamilyManagementTestDataProvider.goodFamilyMemberDTOJson.get();

        when(familyManagementService.signUpNewFamilyMember(any(FamilyMemberDTO.class)))
                .thenReturn(true);

        mockMvc.perform(post(uriTemplate)
                .content(familyMemberDTOJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testSignUpMember_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/signup";
        String expectedOutcome = "Family Member could not be created.";
        String familyMemberDTOJson = FamilyManagementTestDataProvider.goodFamilyMemberDTOJson.get();

        when(familyManagementService.signUpNewFamilyMember(any(FamilyMemberDTO.class)))
                .thenReturn(false);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testActivateNewMember_(){}

    @Test
    void testLoginMember_(){}

    @Test
    void testLoginVerification_(){}

    @Test
    void testBrowseCourses_(){}

}
