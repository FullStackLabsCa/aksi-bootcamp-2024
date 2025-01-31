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
        offeredCourse.setNumOfClassesOffered(offeredCourseDTO.getNumOfClassesOffered());

        offeredCourseRepository.save(offeredCourse);
        return true;
    }

    public OfferedCourseDTO getOfferedCourse(int offeredCourseId) {
        OfferedCourse offeredCourse = getOfferedCourseById(offeredCourseId);

        OfferedCourseDTO offeredCourseDTO = offeredCourseMapper.toDto(offeredCourse);
        offeredCourseDTO.setCourseId(offeredCourse.getCourse().getCourseId());
        offeredCourseDTO.setFacilityId(offeredCourse.getFacility().getFacilityId());
        offeredCourseDTO.setNumOfClassesOffered(offeredCourse.getNumOfClassesOffered());
        offeredCourseDTO.setAllDayCourse(offeredCourse.isAllDayCourse());

        return offeredCourseDTO;
    }

    public OfferedCourse getOfferedCourseById(int offeredCourseId) {
        return offeredCourseRepository.findByOfferedCourseId(offeredCourseId)
                .orElseThrow(() -> new OfferedCourseNotFoundException("OfferedCourse Not Found"));
    }


    public OfferedCourseDTO updateOfferedCourseInfo(OfferedCourseDTO offeredCourseDTO, int offeredCourseId) {
        OfferedCourse offeredCourse = getOfferedCourseById(offeredCourseId);
        offeredCourseMapper.updateOfferedCourseFromDto(offeredCourseDTO, offeredCourse);
        return offeredCourseMapper.toDto(offeredCourseRepository.save(offeredCourse));
    }

    public boolean cancelOfferedCourse(int offeredCourseId) {
        OfferedCourse offeredCourse = getOfferedCourseById(offeredCourseId);
        offeredCourse.setAvailableForEnrollment("CLOSED");
        offeredCourseRepository.save(offeredCourse);
        return true;
    }
}
