package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.LoginUUIDToken;
import org.reactivestax.canada_active_life.domain.SignUpUUIDToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoginUUIDTokenRepository extends JpaRepository<LoginUUIDToken, Long>, JpaSpecificationExecutor<LoginUUIDToken> {
}
