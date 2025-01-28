package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class OfferedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int offeredCourseId;

    @Column(unique = true, nullable = false)
    private String barCode;

    private LocalDate startDate;
    private LocalDate endDate;

    private int numOfClassedOffered;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private boolean isAllDayCourse;

    private LocalDate registrationStartDate;
    private String availableForEnrollment;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course courseId;

    @ManyToOne
    @JoinColumn(name = "facilityId")
    private Facility facilityId;

    @CreationTimestamp
    private LocalDateTime createdTimeStamp;
    private String createdBy;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedTimeStamp;
    private String updatedBy;
}
