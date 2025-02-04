package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyManagementTestDataProvider;
import org.reactivestax.canada_active_life.TestDataProvider.FamilyMemberCourseRegistrationTestDataProvider;
import org.reactivestax.canada_active_life.TestDataProvider.OfferedCourseTestDataProvider;
import org.reactivestax.canada_active_life.domain.*;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseWaitlistDTO;
import org.reactivestax.canada_active_life.enums.FeeType;
import org.reactivestax.canada_active_life.mapper.FamilyCourseRegistrationMapper;
import org.reactivestax.canada_active_life.mapper.FamilyCourseWaitlistMapper;
import org.reactivestax.canada_active_life.repo.FamilyCourseRegistrationRepository;
import org.reactivestax.canada_active_life.repo.FamilyCourseWaitlistRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private RegistrationManagementService registrationManagementService;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @MockitoBean
    private OfferedCourseService offeredCourseService;

    @MockitoBean
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @MockitoBean
    private FamilyCourseWaitlistRepository familyCourseWaitlistRepository;

    @MockitoBean
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @MockitoBean
    private FamilyCourseRegistrationMapper familyCourseRegistrationMapper;

    @MockitoBean
    private FamilyCourseWaitlistMapper familyCourseWaitlistMapper;

    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void testEnrollFamilyMemberInOfferedCourse_InactiveFamilyMember(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_OfferedCourseClosedForEnrollment(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_FamilyMemberAlreadyEnrolledInOfferedCourse(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_SeatsNotAvaliableAndFamilyMemberAlreadyInWaitlist(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_SeatsNotAvailableAndWaitlistFamilyMember(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_SeatsAvailableAndFamilyMemberNotInWaitlist(){
        // Mocks
        when(familyManagementService.checkFamilyMemberValidity(any(String.class)))
                .thenReturn(FamilyManagementTestDataProvider.goodActiveFamilyMember.get());
        when(offeredCourseService.getOfferedCourseById(any(Integer.class)))
                .thenReturn(OfferedCourseTestDataProvider.goodOfferedCourse.get());
        when(familyCourseRegistrationRepository.findAllByOfferedCourse_OfferedCourseIdAndIsWithdrawn(any(Integer.class), any(Boolean.class)))
                .thenReturn(new ArrayList<>());
        when(familyCourseWaitlistRepository.findByOfferedCourse_OfferedCourseIdAndFamilyMember_FamilyMemberIdAndIsWaitlisted(any(Integer.class), any(Integer.class), any(Boolean.class)))
                .thenReturn(Optional.empty());
        when(offeredCourseFeeRepository.findByOfferedCourse_OfferedCourseIdAndFeeType(any(Integer.class), any(FeeType.class)))
                .thenReturn(Optional.of(OfferedCourseFee.builder().courseFee(100).build()));

        // Actions
        assertTrue(registrationManagementService.enrollFamilyMemberInOfferedCourse(FamilyMemberCourseRegistrationTestDataProvider.validRegistrationDTO.get(), "anyActor"));

        // Assert
        verify(familyManagementService, times(2)).checkFamilyMemberValidity(any(String.class));
        verify(familyManagementService, times(0)).sendActivationLink(any(FamilyMember.class));
        verify(offeredCourseService, times(1)).getOfferedCourseById(any(Integer.class));
        verify(familyCourseRegistrationRepository, times(1)).findAllByOfferedCourse_OfferedCourseIdAndIsWithdrawn(any(Integer.class), any(Boolean.class));
        verify(familyCourseWaitlistRepository, times(1)).findByOfferedCourse_OfferedCourseIdAndFamilyMember_FamilyMemberIdAndIsWaitlisted(any(Integer.class), any(Integer.class), any(Boolean.class));
        verify(offeredCourseFeeRepository, times(1)).findByOfferedCourse_OfferedCourseIdAndFeeType(any(Integer.class), any(FeeType.class));
        verify(familyCourseRegistrationRepository, times(1)).save(any(FamilyCourseRegistration.class));
        verify(familyCourseWaitlistRepository, times(0)).save(any(FamilyCourseWaitlist.class));
    }

    @Test
    void testGetEnrollmentsForMember_ValidFamilyMember(){
        when(familyManagementService.checkFamilyMemberValidity(any(String.class)))
                .thenReturn(FamilyManagementTestDataProvider.goodActiveFamilyMember.get());
        List<FamilyCourseRegistration> listOfRegistrations = new ArrayList<>();
        listOfRegistrations.add(FamilyCourseRegistration.builder()
                        .familyMember(FamilyMember.builder().memberLoginId("anyMember").build())
                        .offeredCourse(OfferedCourse.builder().offeredCourseId(1).build())
                .build());
        when(familyCourseRegistrationRepository.findAllByFamilyMember_FamilyMemberIdAndIsWithdrawn(any(Integer.class), any(Boolean.class)))
                .thenReturn(listOfRegistrations);
        when(familyCourseRegistrationMapper.toDto(any(FamilyCourseRegistration.class)))
                .thenReturn(FamilyMemberCourseRegistrationDTO.builder().build());

        List<FamilyMemberCourseRegistrationDTO> registrations = registrationManagementService.getEnrollmentsForMember("anyMember");

        assertNotNull(registrations);
        assertFalse(registrations.isEmpty());
    }

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_NoRegistrationFound(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_RegistrationFoundAndNotifyFailed(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_RegistrationFoundAndNotifyWaitlist(){
        // Setup
        when(familyManagementService.checkFamilyMemberValidity(any(String.class)))
                .thenReturn(FamilyManagementTestDataProvider.goodFamilyMemberAsGroupOwner.get());
        when(familyCourseRegistrationRepository.findByOfferedCourse_OfferedCourseIdAndFamilyMember_MemberLoginIdAndIsWithdrawn(any(Integer.class), any(String.class), any(Boolean.class)))
                .thenReturn(Optional.of(FamilyCourseRegistration.builder().cost(10).build()));
        List<FamilyCourseWaitlist> waitlists = new ArrayList<>();
        waitlists.add(FamilyCourseWaitlist.builder()
                .familyMember(FamilyMember.builder().name("test").homePhoneNumber("123").build())
                        .offeredCourse(OfferedCourse.builder().offeredCourseId(1).build())
                .build());
        when(familyCourseWaitlistRepository.findAllByOfferedCourse_OfferedCourseIdAndIsWaitlisted(any(Integer.class), any(Boolean.class)))
                .thenReturn(waitlists);
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Message Sent Via SMS.", HttpStatus.OK);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponse);

        // Action
        assertTrue(registrationManagementService.withdrawFamilyMemberFromOfferedCourse(1, "anyMember", "anyActor"));

        // Assert
        verify(familyManagementService, times(2)).checkFamilyMemberValidity(any(String.class));
        verify(familyCourseRegistrationRepository, times(1)).findByOfferedCourse_OfferedCourseIdAndFamilyMember_MemberLoginIdAndIsWithdrawn(any(Integer.class), any(String.class) , any(Boolean.class));
        verify(familyCourseRegistrationRepository, times(1)).save(any(FamilyCourseRegistration.class));
        verify(familyCourseWaitlistRepository, times(1)).findAllByOfferedCourse_OfferedCourseIdAndIsWaitlisted(any(Integer.class), any(Boolean.class));
        verify(restTemplate, times(1)).postForEntity(any(String.class), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void testGetWaitlistForMember_ValidFamilyMember(){
        when(familyManagementService.checkFamilyMemberValidity(any(String.class)))
                .thenReturn(FamilyManagementTestDataProvider.goodFamilyMemberAsGroupOwner.get());
        List<FamilyCourseWaitlist> waitlists = new ArrayList<>();
        waitlists.add(FamilyCourseWaitlist.builder()
                .familyMember(FamilyMember.builder().name("test").homePhoneNumber("123").build())
                .offeredCourse(OfferedCourse.builder().offeredCourseId(1).build())
                .build());
        when(familyCourseWaitlistRepository.findAllByFamilyMember_FamilyMemberIdAndIsWaitlisted(any(Integer.class), any(Boolean.class)))
                .thenReturn(waitlists);
        when(familyCourseWaitlistMapper.toDto(any(FamilyCourseWaitlist.class)))
                .thenReturn(FamilyMemberCourseWaitlistDTO.builder().build());

        List<FamilyMemberCourseWaitlistDTO> waitlist = registrationManagementService.getWaitlistForMember("anyMember");
        assertNotNull(waitlist);
        assertFalse(waitlist.isEmpty());
    }

}
