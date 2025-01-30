package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    private String memberLoginId;
    private String familyPin;
}
