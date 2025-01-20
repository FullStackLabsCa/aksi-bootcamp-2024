package org.reactivestax.ems.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.reactivestax.ems.enums.DeliveryMode;
import org.reactivestax.ems.enums.MessageType;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Customer customer;

    private String messageData;
    private MessageType messageType;
    private DeliveryMode deliveryMode;

    @Builder.Default
    private LocalDateTime creationTime = LocalDateTime.now();
    private LocalDateTime updatedTime;
}
