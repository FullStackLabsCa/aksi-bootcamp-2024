package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.mapper.OfferedCourseMapper;
import org.reactivestax.canada_active_life.repo.CourseRepository;
import org.reactivestax.canada_active_life.repo.FacilityRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OfferedCourseServiceTest {

    @Autowired
    private OfferedCourseService offeredCourseService;

    @MockitoBean
    private OfferedCourseMapper offeredCourseMapper;

    @MockitoBean
    private OfferedCourseRepository offeredCourseRepository;

    @MockitoBean
    private CourseRepository courseRepository;

    @MockitoBean
    private FacilityRepository facilityRepository;

    @Test
    void testCreateNewOfferedCourse_InvalidCourse(){}

    @Test
    void testCreateNewOfferedCourse_InvalidFacility(){}

    @Test
    void testCreateNewOfferedCourse_ValidCourseAndFacility(){}

    @Test
    void testGetOfferedCourse_InvalidOfferedCourse(){}

    @Test
    void testGetOfferedCourse_ValidOfferedCourse(){}

    @Test
    void testUpdateOfferedCourse_InvalidOfferedCourse(){}

    @Test
    void testUpdateOfferedCourse_ValidOfferedCourse(){}

    @Test
    void testCancelOfferedCourse_InvalidOfferedCourse(){}

    @Test
    void testCancelOfferedCourse_ValidOfferedCourse(){}

}
