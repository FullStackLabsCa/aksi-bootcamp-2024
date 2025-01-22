package org.reactivestax.ems.repository;

import org.reactivestax.ems.domain.Message;
import org.reactivestax.ems.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
    Optional<Message> findFirstByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfterAndOtpFailureCountLessThanOrderByCreationTimeDesc(String customerId, MessageType messageType, LocalDateTime creationTimeAfter, int otpMaxFailureCount);
    Optional<Message> findFirstByCustomer_CustomerIdAndMessageTypeAndCreationTimeAfterAndOtpFailureCountGreaterThanOrderByCreationTimeDesc(String customerId, MessageType messageType, LocalDateTime creationTimeAfter, int otpMaxFailureCount);
}
