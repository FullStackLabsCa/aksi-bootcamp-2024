package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FamilyCourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyCourseRegistrationId;

    private double cost;
    private LocalDate enrollmentDate;

    @Builder.Default
    private boolean isWithdrawn = false;
    private double withdrawnCredits;

    private String enrollmentActor;
    private int enrollmentActorId;

    @ManyToOne
    @JoinColumn(name = "offeredCourseId")
    private OfferedCourse offeredCourse;

    @ManyToOne
    @JoinColumn(name = "familyMemberId")
    private FamilyMember familyMember;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;
    @UpdateTimestamp
    private LocalDateTime updatedTimeStamp;

    private int createdBy;
    private int updatedBy;
}
