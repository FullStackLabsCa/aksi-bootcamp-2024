package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long>, JpaSpecificationExecutor<FamilyMember> {
    Optional<FamilyMember> findByMemberLoginId(String memberLoginId);
    Optional<FamilyMember> findByFamilyMemberId(int familyMemberId);
}
