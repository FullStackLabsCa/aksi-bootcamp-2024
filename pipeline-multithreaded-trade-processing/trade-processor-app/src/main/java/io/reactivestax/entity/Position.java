package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Table (name = "positions", indexes = {
        @Index(name = "idx_composite_key", columnList = "account_number, security_id")
})
@Data
public class Position {

    @EmbeddedId
    private PositionCompositeKey positionID;

    @Column (name = "position")
    private int positionAmount;

    @Column
    private int version;

}



