package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.UUIDToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UUIDTokenRepository extends JpaRepository<UUIDToken, Long>, JpaSpecificationExecutor<UUIDToken> {
}
