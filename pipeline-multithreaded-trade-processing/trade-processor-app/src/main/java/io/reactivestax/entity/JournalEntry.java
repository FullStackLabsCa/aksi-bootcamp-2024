package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
@Table (name = "journal_entry")
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

    @Column
    private String tradeID;
}
