package org.reactivestax.canada_active_life.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.Course;
import org.reactivestax.canada_active_life.domain.Facility;
import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.exception.CourseNotFoundException;
import org.reactivestax.canada_active_life.exception.OfferedCourseNotFoundException;
import org.reactivestax.canada_active_life.mapper.OfferedCourseMapper;
import org.reactivestax.canada_active_life.repo.CourseRepository;
import org.reactivestax.canada_active_life.repo.FacilityRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OfferedCourseService {

    @Autowired
    private OfferedCourseMapper offeredCourseMapper;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    public boolean createNewOfferedCourse(OfferedCourseDTO offeredCourseDTO) {
        Course course = courseRepository.findByCourseId(offeredCourseDTO.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("No Course found for the given courseId"));
        Facility facility = facilityRepository.findByFacilityId(offeredCourseDTO.getFacilityId())
                .orElseThrow(() -> new CourseNotFoundException("No Facility found for the given facilityId"));

        OfferedCourse offeredCourse = offeredCourseMapper.toEntity(offeredCourseDTO);

        offeredCourse.setCourse(course);
        offeredCourse.setFacility(facility);

        offeredCourseRepository.save(offeredCourse);
        return false;
    }

    public OfferedCourseDTO getOfferedCourse(int offeredCourseId) {
        /**
         *
         */
        OfferedCourse offeredCourse = offeredCourseRepository.findByOfferedCourseId(offeredCourseId)
                .orElseThrow(() -> new OfferedCourseNotFoundException("OfferedCourse Not Found"));
        return offeredCourseMapper.toDto(offeredCourse);
    }

    public OfferedCourseDTO updateOfferedCourseInfo(OfferedCourseDTO offeredCourseDTO) {
        /**
         *
         */
        return null;
    }

    public boolean cancelOfferedCourse(String offeredCourseId) {
        /**
         *
         */
        return false;
    }
}
