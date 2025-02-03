package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.OfferedCourseFee;
import org.reactivestax.canada_active_life.enums.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OfferedCourseFeeRepository extends JpaRepository<OfferedCourseFee, Long>, JpaSpecificationExecutor<OfferedCourseFee> {
    Optional<OfferedCourseFee> findByOfferedCourse_OfferedCourseIdAndFeeType(int offeredCourseId, FeeType feeType);
}
