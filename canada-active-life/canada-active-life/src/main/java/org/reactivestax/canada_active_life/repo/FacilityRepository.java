package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long>, JpaSpecificationExecutor<Facility> {
    Optional<Facility> findByFacilityId(int facilityId);
}
