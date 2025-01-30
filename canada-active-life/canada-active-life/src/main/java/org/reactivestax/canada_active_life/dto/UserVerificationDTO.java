package org.reactivestax.canada_active_life.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserVerificationDTO {
    private String otpEnteredByUser;
}
