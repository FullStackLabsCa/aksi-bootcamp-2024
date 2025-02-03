package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.FamilyCourseWaitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FamilyCourseWaitlistRepository extends JpaRepository<FamilyCourseWaitlist, Long>, JpaSpecificationExecutor<FamilyCourseWaitlist> {
    Optional<FamilyCourseWaitlist> findByOfferedCourse_OfferedCourseIdAndFamilyMember_FamilyMemberIdAndIsWaitlisted(int offeredCourseId, int familyMemberId, boolean isWaitlisted);
    List<FamilyCourseWaitlist> findAllByOfferedCourse_OfferedCourseIdAndIsWaitlisted(int offeredCourseId, boolean isWaitlisted);
    List<FamilyCourseWaitlist> findAllByFamilyMember_FamilyMemberIdAndIsWaitlisted(int familyMemberId, boolean isWaitlisted);
}
