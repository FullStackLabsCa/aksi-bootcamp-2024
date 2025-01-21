package org.reactivestax.ems_processor_app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestax.ems_processor_app.enums.DeliveryMode;
import org.reactivestax.ems_processor_app.enums.MessageType;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId")
    private Customer customer;

    private String messageData;
    private MessageType messageType;
    private DeliveryMode deliveryMode;

    @Builder.Default
    private LocalDateTime creationTime = LocalDateTime.now();
    private LocalDateTime updatedTime;
}

