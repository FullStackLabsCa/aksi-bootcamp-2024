package org.reactivestax.canada_active_life.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FamilyMemberDTO {

    private int id;

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

    private String memberLoginId;

    private String familyPin;
}
