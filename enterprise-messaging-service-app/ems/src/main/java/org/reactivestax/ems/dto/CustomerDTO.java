package org.reactivestax.ems.dto;

import lombok.Data;
import org.reactivestax.ems.MessageType;

import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long id;
    private String customerId;
    private Long phoneNumber;
    private String emailAddress;

    private MessageType messageType;

    private String message;
    private String otp;

    private Boolean verificationStatus;
    private LocalDateTime updatedTime;
}
