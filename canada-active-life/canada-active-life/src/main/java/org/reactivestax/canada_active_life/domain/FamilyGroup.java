package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
public class FamilyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyGroupId;

    private String familyPin;

    @Builder.Default
    private double credits = 0.0;

    @Builder.Default
    private String status = "inactive";
    @Builder.Default
    private long failedLoginAttempts = 0;

    @OneToOne
    @JoinColumn(name = "familyMemberId")
    private FamilyMember groupOwner;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;
    @UpdateTimestamp
    private LocalDateTime updatedTimeStamp;

    private int createdBy;
    private int updateBy;
}
