package org.reactivestax.canada_active_life.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
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

    @Column(unique = true)
    private String memberLoginId;
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "familyGroupId")
    private FamilyGroup familyGroup;
}
