package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table (name="trades_payload", indexes = {
        @Index(name = "idx_trade_id", columnList = "trade_id")
})
@Data
public class RawPayload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_payload_id")
    private int tradePayloadID;

    @Column(name = "trade_id")
    private String tradeID;

    @Column
    private String payload;

    @Column
    private String status;

    @Column
    private String lookupStatus;

    @Column
    private String postedStatus;
}
