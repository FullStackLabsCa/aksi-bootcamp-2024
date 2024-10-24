package io.reactivestax.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PositionCompositeKey implements Serializable {
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "security_id")
    private int securityID;
}
