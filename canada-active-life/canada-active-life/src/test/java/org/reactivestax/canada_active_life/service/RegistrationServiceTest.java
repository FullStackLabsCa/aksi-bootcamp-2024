package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.mapper.FamilyCourseRegistrationMapper;
import org.reactivestax.canada_active_life.mapper.FamilyCourseWaitlistMapper;
import org.reactivestax.canada_active_life.repo.FamilyCourseRegistrationRepository;
import org.reactivestax.canada_active_life.repo.FamilyCourseWaitlistRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private RegistrationManagementService registrationManagementService;

    @MockitoBean
    private FamilyManagementService familyManagementService;

    @Autowired
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
    void testEnrollFamilyMemberInOfferedCourse_InvalidActor(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_InvalidFamilyMember(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_InactiveFamilyMember(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_InvalidOfferedCourseId(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_OfferedCourseClosedForEnrollment(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_FamilyMemberAlreadyEnrolledInOfferedCourse(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_SeatsNotAvaliableAndFamilyMemberAlreadyInWaitlist(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_SeatsNotAvailableAndWaitlistFamilyMember(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_SeatsAvailableAndFamilyMemberInWaitlist(){}

    @Test
    void testEnrollFamilyMemberInOfferedCourse_SeatsAvailableAndFamilyMemberNotInWaitlist(){}

    @Test
    void testGetEnrollmentsForMember_InvalidFamilyMember(){}

    @Test
    void testGetEnrollmentsForMember_ValidFamilyMember(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_InvalidActor(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_InvalidFamilyMember(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_NoRegistrationFound(){}

    @Test
    void testWithdrawFamilyMemberFromOfferedCourse_RegistrationFoundAndNotifyWaitlist(){}

    @Test
    void testGetWaitlistForMember_ValidFamilyMember(){}

    @Test
    void testGetWaitlistForMember_InvalidFamilyMember(){}

}
