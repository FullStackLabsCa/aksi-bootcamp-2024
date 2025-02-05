package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.PendingSignUpUUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PendingSignUpUUIDRepository extends JpaRepository<PendingSignUpUUID, Long>, JpaSpecificationExecutor<PendingSignUpUUID> {
    Optional<PendingSignUpUUID> findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(UUID uuid, int familyMemberId, LocalDateTime expirationThreshold);
    Optional<PendingSignUpUUID> findTopByFamilyMember_FamilyMemberIdOrderByCreationTimeStampDesc(int familyMemberId);
}
