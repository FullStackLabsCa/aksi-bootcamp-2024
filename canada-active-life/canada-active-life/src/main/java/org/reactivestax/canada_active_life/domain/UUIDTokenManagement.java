package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class UUIDTokenManagement {

    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "familyMemberId")
    private FamilyMember familyMember;

    @CreationTimestamp
    private LocalDateTime creationTimeStamp;
}
