package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.FamilyCourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistration, Long>, JpaSpecificationExecutor<FamilyCourseRegistration> {
    List<FamilyCourseRegistration> findAllByOfferedCourse_OfferedCourseIdAndIsWithdrawn(int offeredCourseId, boolean isWithdrawn);
    Optional<FamilyCourseRegistration> findByOfferedCourse_OfferedCourseIdAndFamilyMember_MemberLoginIdAndIsWithdrawn(int offeredCourseId, String memberLoginId, boolean isWithdrawn);
    List<FamilyCourseRegistration> findAllByFamilyMember_FamilyMemberIdAndIsWithdrawn(int familyMemberId, boolean isWithdrawn);
}
