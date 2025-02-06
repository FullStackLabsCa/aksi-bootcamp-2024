package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private double cost;
    private String cardholderName;
    private String cardNumber;
    private String cardExpirationDate;
    private String cardCvv;
}
