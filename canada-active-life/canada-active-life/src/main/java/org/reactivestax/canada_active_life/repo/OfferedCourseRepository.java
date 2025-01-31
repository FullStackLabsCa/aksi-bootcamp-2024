package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.OfferedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfferedCourseRepository extends JpaRepository<OfferedCourse, Long>, JpaSpecificationExecutor<OfferedCourse> {
}
