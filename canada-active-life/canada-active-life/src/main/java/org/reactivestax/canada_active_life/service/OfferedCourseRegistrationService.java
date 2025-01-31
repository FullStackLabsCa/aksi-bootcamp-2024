package org.reactivestax.canada_active_life.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.dto.CourseMemberRegistrationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OfferedCourseRegistrationService {

    public boolean enrollFamilyMemberInOfferedCourse(CourseMemberRegistrationDTO courseMemberRegistrationDTO, String memberLoginId) {
        /**
         * Validate actor by memberLoginId
         * Validate FamilyMemberId and the OfferedCourseId from the DTO and Existence of them in DB
         *      if familyMember not activated throw exception and send an activation link - Optional
         * Check Open Spots in the OfferedCourse
         *      Query the enrollment table for the offeredCourseId and compare with the noOfSeatsOffered of offeredCourse
         *      If seats available:
         *          check if familyMember in the waitlist for the offeredCourse
         *              if found - set isWaitlisted to false for the family member
         *          get the cost of the offeredCourse
         *          withdraw from the familyCredits - Later To be replaced with Payment Gateway
         *          enroll the familyMember
         *      if no seats available:
         *          check if familyMember in the waitlist for the offeredCourse
         *              if found - familyMember already in the waitlist
         *              if not - add the familyMember to the waitlist table for the offeredCourseId
         */
        return false;
    }

    public List<CourseMemberRegistrationDTO> getEnrollmentsForMember(int familyMemberId) {
        /**
         *
         */
        return null;
    }
}
