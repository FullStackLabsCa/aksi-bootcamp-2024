package org.reactivestax.canada_active_life.controller;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;
import org.reactivestax.canada_active_life.service.DashboardService;
import org.reactivestax.canada_active_life.service.FamilyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void testActivateNewMember_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/activate-account";
        String expectedOutcome = "Family Member activated.";

        when(familyManagementService.activateNewSignUp(any(Integer.class), any(UUID.class)))
                .thenReturn(true);

        mockMvc.perform(get(uriTemplate)
                        .param("familyMemberId", "1")
                        .param("uuid", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testActivateNewMember_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/activate-account";
        String expectedOutcome = "Family Member could not be activated.";

        when(familyManagementService.activateNewSignUp(any(Integer.class), any(UUID.class)))
                .thenReturn(false);

        mockMvc.perform(get(uriTemplate)
                    .param("familyMemberId", "1")
                    .param("uuid", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testLoginMember_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/login";
        String familyMemberDTOJson = FamilyManagementTestDataProvider.goodFamilyMemberDTOJson.get();

        when(familyManagementService.loginMember(any(UserLoginDTO.class)))
                .thenReturn(UUID.randomUUID());

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginMember_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/login";
        String familyMemberDTOJson = FamilyManagementTestDataProvider.goodFamilyMemberDTOJson.get();

        when(familyManagementService.loginMember(any(UserLoginDTO.class)))
                .thenReturn(null);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberDTOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginVerification_(){}

    @Test
    void testBrowseCourses_(){}

}
