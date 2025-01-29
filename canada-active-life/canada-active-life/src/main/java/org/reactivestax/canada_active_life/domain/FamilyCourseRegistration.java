package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class FamilyCourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyCourseRegistrationId;

    private double cost;
    private LocalDate enrollmentDate;

    private boolean isWithdrawn;
    private double withdrawnCredits;

    private String enrollmentActor;
    private int enrollmentActorId;

    @ManyToOne
    @JoinColumn(name = "offeredCourseId")
    private OfferedCourse offeredCourseId;

    @ManyToOne
    @JoinColumn(name = "familyMemberId")
    private FamilyMember familyMemberId;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;
    @UpdateTimestamp
    private LocalDateTime updatedTimeStamp;

    private int createdBy;
    private int updatedBy;
}
