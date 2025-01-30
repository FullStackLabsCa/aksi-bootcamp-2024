package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.PendingLoginUUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PendingLoginUUIDRepository extends JpaRepository<PendingLoginUUID, Long>, JpaSpecificationExecutor<PendingLoginUUID> {
}
