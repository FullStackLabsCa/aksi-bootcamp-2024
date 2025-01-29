package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@Entity
@Data
public class Capability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int capabilityId;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;

    @UpdateTimestamp
    private LocalDateTime updatedTimeStamp;

    private int createdBy;
    private int updatedBy;
}
