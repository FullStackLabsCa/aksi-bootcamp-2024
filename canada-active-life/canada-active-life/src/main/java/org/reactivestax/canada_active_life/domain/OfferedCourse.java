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
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int offeredCourseId;

    @Column(unique = true)
    private String barCode = UUID.randomUUID().toString();

    private LocalDate startDate;
    private LocalDate endDate;

    private int numOfClassesOffered;
    private int seatsAvailable;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private boolean isAllDayCourse;

    private LocalDate registrationStartDate;
    private String availableForEnrollment;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "facilityId")
    private Facility facility;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;
    @UpdateTimestamp
    private LocalDateTime lastUpdatedTimeStamp;

    private int createdBy;
    private int updatedBy;
}
