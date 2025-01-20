package org.reactivestax.ems.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String customerId;
    private String message;
    private String otp;
    private Boolean verificationStatus;
    private Long phoneNumber;
    private String emailAddress;
}
