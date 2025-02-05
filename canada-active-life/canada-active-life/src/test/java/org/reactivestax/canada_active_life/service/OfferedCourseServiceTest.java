package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.reactivestax.canada_active_life.domain.Course;
import org.reactivestax.canada_active_life.domain.Facility;
import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.mapper.OfferedCourseMapper;
import org.reactivestax.canada_active_life.repo.CourseRepository;
import org.reactivestax.canada_active_life.repo.FacilityRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void testCreateNewOfferedCourse_ValidCourseAndFacility(){
        when(courseRepository.findByCourseId(any(Integer.class)))
                .thenReturn(Optional.of(Course.builder().build()));
        when(facilityRepository.findByFacilityId(any(Integer.class)))
                .thenReturn(Optional.of(Facility.builder().build()));
        when(offeredCourseMapper.toEntity(any(OfferedCourseDTO.class)))
                .thenReturn(OfferedCourse.builder().build());

        assertTrue(offeredCourseService.createNewOfferedCourse(OfferedCourseDTO.builder().courseId(1).facilityId(1).numOfClassesOffered(2).build()));

        verify(courseRepository, times(1)).findByCourseId(any(Integer.class));
        verify(facilityRepository, times(1)).findByFacilityId(any(Integer.class));
        verify(offeredCourseMapper, times(1)).toEntity(any(OfferedCourseDTO.class));
        verify(offeredCourseRepository, times(1)).save(any(OfferedCourse.class));
    }

    @Test
    void testGetOfferedCourse_InvalidOfferedCourse(){}

    @Test
    void testGetOfferedCourse_ValidOfferedCourse(){
        when(offeredCourseRepository.findByOfferedCourseId(any(Integer.class)))
                .thenReturn(Optional.of(OfferedCourse.builder()
                                .course(Course.builder().courseId(1).build())
                                .facility(Facility.builder().facilityId(1).build())
                                .numOfClassesOffered(2)
                                .isAllDayCourse(true)
                        .build()));
        when(offeredCourseMapper.toDto(any(OfferedCourse.class)))
                .thenReturn(OfferedCourseDTO.builder().build());

        OfferedCourseDTO offeredCourse = offeredCourseService.getOfferedCourse(1);

        assertNotNull(offeredCourse);
        verify(offeredCourseRepository, times(1)).findByOfferedCourseId(any(Integer.class));
        verify(offeredCourseMapper, times(1)).toDto(any(OfferedCourse.class));
    }

    @Test
    void testUpdateOfferedCourse_ValidOfferedCourse(){
        when(offeredCourseRepository.findByOfferedCourseId(any(Integer.class)))
                .thenReturn(Optional.of(OfferedCourse.builder()
                        .course(Course.builder().courseId(1).build())
                        .facility(Facility.builder().facilityId(1).build())
                        .numOfClassesOffered(2)
                        .isAllDayCourse(true)
                        .build()));
        doNothing().when(offeredCourseMapper).updateOfferedCourseFromDto(any(OfferedCourseDTO.class), any(OfferedCourse.class));
        when(offeredCourseRepository.save(any(OfferedCourse.class)))
                .thenReturn(OfferedCourse.builder().build());
        when(offeredCourseMapper.toDto(any(OfferedCourse.class)))
                .thenReturn(OfferedCourseDTO.builder().build());

        OfferedCourseDTO offeredCourse = offeredCourseService.updateOfferedCourseInfo(OfferedCourseDTO.builder().build(), 1);

        assertNotNull(offeredCourse);
        verify(offeredCourseRepository, times(1)).findByOfferedCourseId(any(Integer.class));
        verify(offeredCourseMapper, times(1)).updateOfferedCourseFromDto(any(OfferedCourseDTO.class), any(OfferedCourse.class));
        verify(offeredCourseMapper, times(1)).toDto(any(OfferedCourse.class));
        verify(offeredCourseRepository, times(1)).save(any(OfferedCourse.class));
    }

    @Test
    void testCancelOfferedCourse_ValidOfferedCourse(){
        when(offeredCourseRepository.findByOfferedCourseId(any(Integer.class)))
                .thenReturn(Optional.of(OfferedCourse.builder()
                        .course(Course.builder().courseId(1).build())
                        .facility(Facility.builder().facilityId(1).build())
                        .numOfClassesOffered(2)
                        .isAllDayCourse(true)
                        .build()));

        assertTrue(offeredCourseService.cancelOfferedCourse(1));
        verify(offeredCourseRepository, times(1)).findByOfferedCourseId(any(Integer.class));
        verify(offeredCourseRepository, times(1)).save(any(OfferedCourse.class));
    }

}
