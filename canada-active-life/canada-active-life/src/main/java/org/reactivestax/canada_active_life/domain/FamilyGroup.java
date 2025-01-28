package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class FamilyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyGroupId;

    private String familyPin;

    private double credits;

    private String status;

    private long failedLoginAttempts;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;

    private String createdBy;

    private LocalDateTime updatedTimeStamp;

    private String updateBy;
}
