package org.reactivestax.canada_active_life.repo;

import org.reactivestax.canada_active_life.domain.UnconfirmedPaymentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface UnconfirmedPaymentRegistrationRepository extends JpaRepository<UnconfirmedPaymentRegistration, Long>, JpaSpecificationExecutor<UnconfirmedPaymentRegistration> {
    List<UnconfirmedPaymentRegistration> findAllByOfferedCourse_OfferedCourseIdAndCreationTimeStampAfter(int offeredCourseId, LocalDateTime expirationTime);
}
