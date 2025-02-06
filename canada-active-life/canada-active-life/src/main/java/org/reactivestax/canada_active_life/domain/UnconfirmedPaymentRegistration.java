package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnconfirmedPaymentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int unconfirmedPaymentRegistrationId;

    private int enrollmentActorId;

    @ManyToOne
    @JoinColumn(name = "offeredCourseId")
    private OfferedCourse offeredCourse;

    @ManyToOne
    @JoinColumn(name = "familyMemberId")
    private FamilyMember familyMember;

    private double cost;

    @CreationTimestamp
    private LocalDateTime creationTimeStamp;
}
