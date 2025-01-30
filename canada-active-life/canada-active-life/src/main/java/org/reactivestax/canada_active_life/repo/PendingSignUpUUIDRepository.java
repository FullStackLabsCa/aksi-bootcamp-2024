package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.PendingSignUpUUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PendingSignUpUUIDRepository extends JpaRepository<PendingSignUpUUID, Long>, JpaSpecificationExecutor<PendingSignUpUUID> {
}
