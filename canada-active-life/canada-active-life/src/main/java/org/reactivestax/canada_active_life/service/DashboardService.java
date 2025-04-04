package org.reactivestax.canada_active_life.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.dto.SearchCriteriaDTO;
import org.reactivestax.canada_active_life.mapper.OfferedCourseMapper;
import org.reactivestax.canada_active_life.repo.OfferedCourseRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final OfferedCourseRepository offeredCourseRepository;
    private final OfferedCourseMapper offeredCourseMapper;

    public List<OfferedCourseDTO> browseCourses(SearchCriteriaDTO searchCriteriaDTO) {
        Specification<OfferedCourse> specification = Specification.where(null);

        specification = specification
                .and(OfferedCourseSpec.isAvailableForEnrollment(searchCriteriaDTO.getAvailableForEnrollment()))
                .and(OfferedCourseSpec.forAgeGroups(searchCriteriaDTO.getAgeGroupIds()))
                .and(OfferedCourseSpec.inFacilities(searchCriteriaDTO.getFacilityIds()))
                .and(OfferedCourseSpec.inSubCategories(searchCriteriaDTO.getSubCategoryIds()))
                .and(OfferedCourseSpec.inCategories(searchCriteriaDTO.getCategoryIds()))
                .and(OfferedCourseSpec.withinDates(searchCriteriaDTO.getStartDate(), searchCriteriaDTO.getEndDate()));

        List<OfferedCourse> offeredCourses = offeredCourseRepository.findAll(specification);
        List<OfferedCourseDTO> offeredCourseDTOS = new ArrayList<>();
        for (OfferedCourse offeredCourse : offeredCourses){
            OfferedCourseDTO offeredCourseDTO = offeredCourseMapper.toDto(offeredCourse);
            offeredCourseDTO.setCourse(offeredCourse.getCourse());
            offeredCourseDTO.setFacility(offeredCourse.getFacility());
            offeredCourseDTO.setCourseId(offeredCourse.getOfferedCourseId());
            offeredCourseDTO.setFacilityId(offeredCourse.getFacility().getFacilityId());

            offeredCourseDTOS.add(offeredCourseDTO);
        }
        return offeredCourseDTOS;
    }
}
