package org.reactivestax.ems.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.reactivestax.ems.MessageType;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private Long phoneNumber;
    private String emailAddress;

    private MessageType messageType;

    private String message;
    private String otp;

    private Boolean verificationStatus;
}
