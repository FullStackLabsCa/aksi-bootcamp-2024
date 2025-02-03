package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;


@Entity
@Table (name="trades_payload", indexes = {
        @Index(name = "idx_raw_payload_trade_id", columnList = "trade_id")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawPayload that = (RawPayload) o;
        return Objects.equals(tradeID, that.tradeID) && Objects.equals(payload, that.payload) && Objects.equals(status, that.status) && Objects.equals(lookupStatus, that.lookupStatus) && Objects.equals(postedStatus, that.postedStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeID, payload, status, lookupStatus, postedStatus);
    }
}
