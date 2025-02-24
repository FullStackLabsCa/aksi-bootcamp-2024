package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginVerificationResponseDTO {
    private boolean isVerified;
    private String jwtToken;
}
