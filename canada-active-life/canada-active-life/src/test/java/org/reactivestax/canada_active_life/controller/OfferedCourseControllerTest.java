package org.reactivestax.canada_active_life.controller;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyMemberCourseRegistrationTestDataProvider;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.service.OfferedCourseService;
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

@WebMvcTest(OfferedCourseController.class)
class OfferedCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferedCourseService offeredCourseService;

    @Test
    void testCreateNewOfferedCourse_Successful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/offeredCourses";
        String expectedOutcome = "Offered Course created.";
        String familyMemberCourseRegistrationDTO = FamilyMemberCourseRegistrationTestDataProvider.validRegistrationDTOJson.get();

        when(offeredCourseService.createNewOfferedCourse(any(OfferedCourseDTO.class)))
                .thenReturn(true);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberCourseRegistrationDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutcome));
    }

    @Test
    void testCreateNewOfferedCourse_Unsuccessful() throws Exception {
        String uriTemplate = "/CanadaActiveLife/v1/offeredCourses";
        String expectedOutcome = "Offered Course could not be created.";
        String familyMemberCourseRegistrationDTO = FamilyMemberCourseRegistrationTestDataProvider.validRegistrationDTOJson.get();

        when(offeredCourseService.createNewOfferedCourse(any(OfferedCourseDTO.class)))
                .thenReturn(false);

        mockMvc.perform(post(uriTemplate)
                        .content(familyMemberCourseRegistrationDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedOutcome));
    }

}
