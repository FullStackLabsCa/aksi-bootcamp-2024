package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long>, JpaSpecificationExecutor<FamilyMember> {
}
