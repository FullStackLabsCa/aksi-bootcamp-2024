package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(PositionId.class)
@Table (name = "positions")
public class Position {

    @EmbeddedId
    private int positionID;

    @Column
    private String accountNumber;

    @Column (name = "security_id")
    private int securityID;

    @Column
    private int position;

    @Column
    private int version;

}

