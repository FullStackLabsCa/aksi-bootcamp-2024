package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table (name = "positions")
@Data
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int positionID;

    @Column (name = "account_number")
    private String accountNumber;

    @Column (name = "security_id")
    private int securityID;

    @Column (name = "position")
    private int positionAmount;

    @Column
    private int version;

}



