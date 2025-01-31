package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.reactivestax.canada_active_life.enums.FeeType;

import java.time.LocalDateTime;

@Entity
@Data
public class OfferedCourseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feeId;

    private FeeType feeType;
    private double courseFee;

    @ManyToOne
    @JoinColumn(name = "offeredCourseId")
    private OfferedCourse offeredCourse;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;
    @UpdateTimestamp
    private LocalDateTime updatedTimeStamp;

    private int createdBy;
    private int updatedBy;
}
