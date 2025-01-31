package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByCourseId(int courseId);
}
