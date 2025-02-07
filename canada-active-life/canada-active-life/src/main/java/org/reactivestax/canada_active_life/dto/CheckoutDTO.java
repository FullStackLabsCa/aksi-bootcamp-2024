package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutDTO {
    private double totalCost;

    @Builder.Default
    private Map<Integer, Boolean> cartItemValidity = new HashMap<>();
}
