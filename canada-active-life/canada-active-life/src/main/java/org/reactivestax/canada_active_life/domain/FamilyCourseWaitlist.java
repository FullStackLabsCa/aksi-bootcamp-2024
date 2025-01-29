package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class FamilyCourseWaitlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyCourseWaitlistId;

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
