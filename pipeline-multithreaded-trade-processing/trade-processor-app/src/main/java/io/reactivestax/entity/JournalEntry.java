package io.reactivestax.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
@Table
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "journal_entry_id")
    private int journalEntryID;

    @Column
    private String accountNumber;

    @Column (name = "security_id")
    private String securityID;

    @Column (name = "direction")
    private String activity;

    @Column
    private int quantity;

    @Column
    private Date tradeExecutionTime;

    @Column
    private String positionPostedStatus;
}
