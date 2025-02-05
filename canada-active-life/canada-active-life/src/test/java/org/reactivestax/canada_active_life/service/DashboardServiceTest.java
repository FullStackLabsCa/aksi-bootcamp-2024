package org.reactivestax.canada_active_life.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestax.canada_active_life.domain.Facility;
import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.dto.SearchCriteriaDTO;
import org.reactivestax.canada_active_life.mapper.OfferedCourseMapper;
import org.reactivestax.canada_active_life.repo.OfferedCourseRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @MockitoBean
    private OfferedCourseRepository offeredCourseRepository;

    @MockitoBean
    private OfferedCourseMapper offeredCourseMapper;

    @Test
    void testBrowseCourses(){
        List<OfferedCourse> offeredCourses = new ArrayList<>();
        offeredCourses.add(OfferedCourse.builder()
                        .offeredCourseId(1)
                        .facility(Facility.builder().facilityId(1).build())
                .build());
        when(offeredCourseRepository.findAll(any(Specification.class)))
                .thenReturn(offeredCourses);
        when(offeredCourseMapper.toDto(any(OfferedCourse.class)))
                .thenReturn(OfferedCourseDTO.builder().build());
        Specification<OfferedCourse> mockedSpecification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        try (MockedStatic<OfferedCourseSpec> offeredCourseSpecMockedStatic = Mockito.mockStatic(OfferedCourseSpec.class)){
            offeredCourseSpecMockedStatic.when(() -> OfferedCourseSpec.isAvailableForEnrollment(any(String.class))).thenReturn(mockedSpecification);
            offeredCourseSpecMockedStatic.when(() -> OfferedCourseSpec.inCities(any())).thenReturn(mockedSpecification);
            offeredCourseSpecMockedStatic.when(() -> OfferedCourseSpec.inFacilities(any())).thenReturn(mockedSpecification);
            offeredCourseSpecMockedStatic.when(() -> OfferedCourseSpec.forAgeGroups(any())).thenReturn(mockedSpecification);
            offeredCourseSpecMockedStatic.when(() -> OfferedCourseSpec.inSubCategories(any())).thenReturn(mockedSpecification);
            offeredCourseSpecMockedStatic.when(() -> OfferedCourseSpec.inCategories(any())).thenReturn(mockedSpecification);
            offeredCourseSpecMockedStatic.when(() -> OfferedCourseSpec.withinDates(any(LocalDate.class), any(LocalDate.class))).thenReturn(mockedSpecification);

            List<OfferedCourseDTO> offeredCourseDTOS = dashboardService.browseCourses(SearchCriteriaDTO.builder().build());

            assertNotNull(offeredCourseDTOS);
            assertFalse(offeredCourseDTOS.isEmpty());
        }
    }
}
