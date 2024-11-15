package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.Objects;

@Entity
@Data
@Table (name = "journal_entry", indexes = {
        @Index(name = "idx_trade_id", columnList = "trade_id")
})
@Builder
@AllArgsConstructor
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "journal_entry_id")
    private int journalEntryID;

    @Column
    private String accountNumber;

    @Column (name = "security_id")
    private int securityID;

    @Column (name = "direction")
    private String activity;

    @Column
    private int quantity;

    @Column
    private Date tradeExecutionTime;

    @Column
    private String positionPostedStatus;

    @Column (name = "trade_id")
    private String tradeID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JournalEntry that = (JournalEntry) o;
        return securityID == that.securityID && quantity == that.quantity && Objects.equals(accountNumber, that.accountNumber) && Objects.equals(activity, that.activity) && Objects.equals(tradeExecutionTime, that.tradeExecutionTime) && Objects.equals(positionPostedStatus, that.positionPostedStatus) && Objects.equals(tradeID, that.tradeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, securityID, activity, quantity, tradeExecutionTime, positionPostedStatus, tradeID);
    }
}
