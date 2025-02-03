package org.reactivestax.canada_active_life.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.*;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseRegistrationDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberCourseWaitlistDTO;
import org.reactivestax.canada_active_life.enums.FeeType;
import org.reactivestax.canada_active_life.exception.*;
import org.reactivestax.canada_active_life.mapper.FamilyCourseRegistrationMapper;
import org.reactivestax.canada_active_life.mapper.FamilyCourseWaitlistMapper;
import org.reactivestax.canada_active_life.repo.FamilyCourseRegistrationRepository;
import org.reactivestax.canada_active_life.repo.FamilyCourseWaitlistRepository;
import org.reactivestax.canada_active_life.repo.OfferedCourseFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RegistrationManagementService {

    @Autowired
    private FamilyManagementService familyManagementService;

    @Autowired
    private OfferedCourseService offeredCourseService;

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Autowired
    private FamilyCourseWaitlistRepository familyCourseWaitlistRepository;

    @Autowired
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @Autowired
    private FamilyCourseRegistrationMapper familyCourseRegistrationMapper;

    @Autowired
    private FamilyCourseWaitlistMapper familyCourseWaitlistMapper;

    public boolean enrollFamilyMemberInOfferedCourse(FamilyMemberCourseRegistrationDTO familyMemberCourseRegistrationDTO, String memberLoginId) {
        /**
         * Validate actor by memberLoginId - Done
         * Validate FamilyMemberId and the OfferedCourseId from the DTO and Existence of them in DB - Done
         *      if familyMember not activated throw exception - Done
         *          send a signUp activation link - TODO
         * Check if course is available for Enrollment
         * Check Open Spots in the OfferedCourse - Done
         *      Query the enrollment table for the offeredCourseId and compare with the noOfSeatsOffered of offeredCourse - Done
         *      If member already enrolled notify - Done
         *      If seats available: - Done
         *          check if familyMember in the waitlist for the offeredCourse - Done
         *              if found - set isWaitlisted to false for the family member - Done
         *          get the cost of the offeredCourse - Done
         *          withdraw from the familyCredits - Later To be replaced with Payment Gateway - Done
         *          enroll the familyMember - Done
         *      if no seats available: - Done
         *          check if familyMember in the waitlist for the offeredCourse - Done
         *              if found - familyMember already in the waitlist - Done
         *              if not - add the familyMember to the waitlist table for the offeredCourseId - Done
         */
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(memberLoginId);
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(familyMemberCourseRegistrationDTO.getFamilyMemberLoginId());
        if(!familyMember.isActive()) throw new FamilyMemberNotActivatedException("Please verify family member using the activation link sent via email...");

        OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(familyMemberCourseRegistrationDTO.getOfferedCourseId());
        if("CLOSED".equals(offeredCourse.getAvailableForEnrollment())) throw new OfferedCourseNotAvailableForEnrollmentException("Offered Course not available for enrollment.");
        int totalOfferedSeats = offeredCourse.getSeatsAvailable();

        List<FamilyCourseRegistration> enrollmentsForOfferedCourse = familyCourseRegistrationRepository.findAllByOfferedCourse_OfferedCourseIdAndIsWithdrawn(offeredCourse.getOfferedCourseId(), false);
        checkIfFamilyMemberAlreadyEnrolledInOfferedCourse(enrollmentsForOfferedCourse, familyMember);
        Optional<FamilyCourseWaitlist> familyCourseWaitlist = checkIfFamilyMemberInWaitlist(offeredCourse, familyMember);


        int seatsTaken = 0;
        if(!enrollmentsForOfferedCourse.isEmpty()) seatsTaken = enrollmentsForOfferedCourse.size();

        if(seatsTaken < totalOfferedSeats){
            familyCourseWaitlist.ifPresent(courseWaitlist -> courseWaitlist.setWaitlisted(false));
            return performEnrollment(actor, familyMember, offeredCourse);
        } else {
            familyCourseWaitlist.ifPresent(value -> {throw new MemberAlreadyWaitlistedForOfferedCourseException("Family Member is already wailisted in the offered course.");});
            if(waitlistFamilyMember(actor, familyMember, offeredCourse)) throw new FamilyMemberWaitlistedForOfferedCourse("Course Full! Family Member has been waitlisted for the offered course. You will get notification as spot comes available.");
        }
        return false;
    }

    private void checkIfFamilyMemberAlreadyEnrolledInOfferedCourse(List<FamilyCourseRegistration> enrollmentsForOfferedCourse, FamilyMember familyMember) {
        for(FamilyCourseRegistration familyCourseRegistration : enrollmentsForOfferedCourse){
            if(familyCourseRegistration.getFamilyMember().getFamilyMemberId() == familyMember.getFamilyMemberId())
                throw new MemberAlreadyEnrolledInOfferedCourseException("Family Member is already enrolled in the offered course.");
        }
    }

    private Optional<FamilyCourseWaitlist> checkIfFamilyMemberInWaitlist(OfferedCourse offeredCourse, FamilyMember familyMember) {
        return familyCourseWaitlistRepository.findByOfferedCourse_OfferedCourseIdAndFamilyMember_FamilyMemberIdAndIsWaitlisted(offeredCourse.getOfferedCourseId(), familyMember.getFamilyMemberId(), true);
    }

    private boolean performEnrollment(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse) {
        FeeType feeType = FeeType.NON_RESIDENT;
        if(familyMember.getCity().equals(offeredCourse.getFacility().getCity())) feeType = FeeType.RESIDENT;

        double costOfOfferedCourse = getCostOfOfferedCourse(offeredCourse.getOfferedCourseId(), feeType);

        FamilyCourseRegistration enrollment = FamilyCourseRegistration.builder()
                .cost(costOfOfferedCourse)
                .enrollmentActorId(actor.getFamilyMemberId())
                .enrollmentActor(actor.getName())
                .enrollmentDate(LocalDate.now())
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .build();

        // withdraw from FamilyCredits - TODO

        familyCourseRegistrationRepository.save(enrollment);
        return true;
    }

    private double getCostOfOfferedCourse(int offeredCourseId, FeeType feeType) {
        Optional<OfferedCourseFee> offeredCourseFee = offeredCourseFeeRepository.findByOfferedCourse_OfferedCourseIdAndFeeType(offeredCourseId, feeType);
        if(offeredCourseFee.isPresent()) return offeredCourseFee.get().getCourseFee();
        else throw new OfferedCourseFeeNotFoundException("Offered Course Fee could not be found right now.");
    }

    private boolean waitlistFamilyMember(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse) {
        FamilyCourseWaitlist waitlist = FamilyCourseWaitlist.builder()
                .enrollmentActor(actor.getName())
                .enrollmentActorId(actor.getFamilyMemberId())
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .build();

        familyCourseWaitlistRepository.save(waitlist);
        return true;
    }

    public List<FamilyMemberCourseRegistrationDTO> getEnrollmentsForMember(String memberLoginId) {
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(memberLoginId);
        List<FamilyCourseRegistration> enrollmentsForFamilyMember = familyCourseRegistrationRepository.findAllByFamilyMember_FamilyMemberIdAndIsWithdrawn(familyMember.getFamilyMemberId(), false);
        List<FamilyMemberCourseRegistrationDTO> enrollmentsForFamilyMemberDTO = new ArrayList<>();
        for (FamilyCourseRegistration familyCourseRegistration : enrollmentsForFamilyMember){
            FamilyMemberCourseRegistrationDTO dto = familyCourseRegistrationMapper.toDto(familyCourseRegistration);
            dto.setOfferedCourseId(familyCourseRegistration.getOfferedCourse().getOfferedCourseId());
            dto.setFamilyMemberLoginId(familyCourseRegistration.getFamilyMember().getMemberLoginId());
            enrollmentsForFamilyMemberDTO.add(dto);
        }
        return enrollmentsForFamilyMemberDTO;
    }

    public boolean withdrawFamilyMemberFromOfferedCourse(int offeredCourseId, String memberLoginId, String actorMemberLoginId) {
        /**
         * Validate actor by memberLoginId - Optional - Done
         * Validate FamilyMemberId and the OfferedCourseId from the DTO and Existence of them in DB - Optional - Done
         * Find the registration based on the familyMemberLoginId and the OfferedCourseId - Done
         *      is Withdraw allowed? - Pending Requirements
         *      update the creditsWithdrawn based on the business requirements - Pending Requirements
         * isWithdrawn = true - Done
         * get the Waitlist for the offeredCourse - Done
         * Notify all the members in the waitlist for the OfferedCourse Availability - TODO
         */

         familyManagementService.checkFamilyMemberValidity(actorMemberLoginId);
         FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(memberLoginId);
        // OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(offeredCourseId);

        FamilyCourseRegistration registration = familyCourseRegistrationRepository.findByOfferedCourse_OfferedCourseIdAndFamilyMember_MemberLoginIdAndIsWithdrawn(offeredCourseId, memberLoginId, false)
                .orElseThrow(() -> new RegistrationNotFoundException("No Registration found for the Offered Course and the Family Member."));

        registration.setWithdrawn(true);
        registration.setWithdrawnCredits(registration.getCost());
        familyMember.getFamilyGroup().setCredits(familyMember.getFamilyGroup().getCredits() + registration.getCost());

        familyCourseRegistrationRepository.save(registration);

        List<FamilyCourseWaitlist> waitlists = familyCourseWaitlistRepository.findAllByOfferedCourse_OfferedCourseIdAndIsWaitlisted(offeredCourseId, true);
        for (FamilyCourseWaitlist familyCourseWaitlist : waitlists){
            sendCourseAvailabilityNotificationViaEms(familyCourseWaitlist.getFamilyMember().getFamilyMemberId());
        }

        return true;
    }

    private void sendCourseAvailabilityNotificationViaEms(int familyMemberId) {
        log.info("Sending notification to {}", familyMemberId);
    }

    public List<FamilyMemberCourseWaitlistDTO> getWaitlistForMember(String memberLoginId) {
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(memberLoginId);
        List<FamilyCourseWaitlist> waitlistedOfferedCoursesForFamilyMember = familyCourseWaitlistRepository.findAllByFamilyMember_FamilyMemberIdAndIsWaitlisted(familyMember.getFamilyMemberId(), true);
        List<FamilyMemberCourseWaitlistDTO> waitlistedOfferedCoursesForFamilyMemberDTO = new ArrayList<>();
        for (FamilyCourseWaitlist familyCourseWaitlist : waitlistedOfferedCoursesForFamilyMember){
            waitlistedOfferedCoursesForFamilyMemberDTO.add(familyCourseWaitlistMapper.toDto(familyCourseWaitlist));
        }
        return waitlistedOfferedCoursesForFamilyMemberDTO;
    }
}
