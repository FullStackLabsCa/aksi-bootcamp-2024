package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyMemberId;

    private String name;
    private LocalDate dob;
    private String gender;

    private String emailAddress;
    private String streetNumber;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String homePhoneNumber;
    private String businessPhoneNumber;

    private String preferredContactMethod;

    private String language;

    @Column(unique = true, nullable = false)
    private String memberLoginId;

    @Builder.Default
    private boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "familyGroupId")
    private FamilyGroup familyGroup;
}
