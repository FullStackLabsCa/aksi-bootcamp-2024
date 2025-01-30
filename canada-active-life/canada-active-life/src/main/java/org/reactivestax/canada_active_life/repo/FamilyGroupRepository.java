package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FamilyGroupRepository extends JpaRepository<FamilyGroup, Long>, JpaSpecificationExecutor<FamilyGroup> {
}
