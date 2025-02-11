package org.reactivestax.canada_active_life.service;

import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.*;
import org.reactivestax.canada_active_life.dto.CustomerDTO;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

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

    @Autowired
    private RestTemplate restTemplate;

//    public boolean enrollFamilyMemberInOfferedCourse(FamilyMemberCourseRegistrationDTO familyMemberCourseRegistrationDTO, String memberLoginId) {
//        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(memberLoginId);
//        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(familyMemberCourseRegistrationDTO.getFamilyMemberLoginId());
//        if(!familyMember.isActive()) {
//            familyManagementService.sendActivationLink(familyMember);
//            throw new FamilyMemberNotActivatedException("Please verify family member using the activation link sent via sms...");
//        }
//
//        OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(familyMemberCourseRegistrationDTO.getOfferedCourseId());
//        if("CLOSED".equals(offeredCourse.getAvailableForEnrollment())) throw new OfferedCourseNotAvailableForEnrollmentException("Offered Course not available for enrollment.");
//        int totalOfferedSeats = offeredCourse.getSeatsAvailable();
//
//        List<FamilyCourseRegistration> enrollmentsForOfferedCourse = getEnrollmentsForOfferedCourse(offeredCourse);
//        checkIfFamilyMemberAlreadyEnrolledInOfferedCourse(enrollmentsForOfferedCourse, familyMember);
//        Optional<FamilyCourseWaitlist> familyCourseWaitlist = checkIfFamilyMemberInWaitlist(offeredCourse, familyMember);
//
//
//        int seatsTaken = enrollmentsForOfferedCourse.size();
//
//        if(seatsTaken < totalOfferedSeats){
//            familyCourseWaitlist.ifPresent(courseWaitlist -> courseWaitlist.setWaitlisted(false));
//            return performEnrollment(actor, familyMember, offeredCourse);
//        } else {
//            familyCourseWaitlist.ifPresent(value -> {throw new MemberAlreadyWaitlistedForOfferedCourseException("Family Member is already wailisted in the offered course.");});
//            if(waitlistFamilyMember(actor.getFamilyMemberId(), familyMember, offeredCourse)) throw new FamilyMemberWaitlistedForOfferedCourse("Course Full! Family Member has been waitlisted for the offered course. You will get notification as spot comes available.");
//        }
//        return false;
//    }

    public boolean enrollFamilyMemberInOfferedCourse(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse){
        Optional<FamilyCourseWaitlist> familyCourseWaitlist = checkIfFamilyMemberInWaitlist(offeredCourse, familyMember);

        if(checkFamilyMemberAndOfferedCourseValidity(familyMember, offeredCourse)){
            familyCourseWaitlist.ifPresent(courseWaitlist -> courseWaitlist.setWaitlisted(false));
            return performEnrollment(actor, familyMember, offeredCourse);
        } else {
            if (familyCourseWaitlist.isPresent()) {throw new FamilyMemberWaitlistedForOfferedCourse("Family Member is already waitlisted in the offered course.");}
            throw new OfferedCourseNotAvailableForEnrollmentException("No Seats available in the offered course. Would you like to waitlist?");
        }
    }

    private Optional<FamilyCourseWaitlist> checkIfFamilyMemberInWaitlist(OfferedCourse offeredCourse, FamilyMember familyMember) {
        return familyCourseWaitlistRepository.findByOfferedCourse_OfferedCourseIdAndFamilyMember_FamilyMemberIdAndIsWaitlisted(offeredCourse.getOfferedCourseId(), familyMember.getFamilyMemberId(), true);
    }

    public boolean checkFamilyMemberAndOfferedCourseValidity(FamilyMember familyMember, OfferedCourse offeredCourse){
        if(!familyMember.isActive()) {
            familyManagementService.sendActivationLink(familyMember);
            throw new FamilyMemberNotActivatedException("Family Member Not Activated. Please verify family member using the activation link sent via sms...");
        }

        if("CLOSED".equals(offeredCourse.getAvailableForEnrollment())) {
            throw new OfferedCourseNotAvailableForEnrollmentException("Offered Course is now closed for Enrollment.");
        }
        int totalOfferedSeats = offeredCourse.getSeatsAvailable();

        List<FamilyCourseRegistration> enrollmentsForOfferedCourse = getEnrollmentsForOfferedCourse(offeredCourse);
        checkIfFamilyMemberAlreadyEnrolledInOfferedCourse(enrollmentsForOfferedCourse, familyMember);

        int seatsTaken = enrollmentsForOfferedCourse.size();
        return seatsTaken < totalOfferedSeats;
    }

    private List<FamilyCourseRegistration> getEnrollmentsForOfferedCourse(OfferedCourse offeredCourse) {
        return familyCourseRegistrationRepository.findAllByOfferedCourse_OfferedCourseIdAndIsWithdrawn(offeredCourse.getOfferedCourseId(), false);
    }

    private void checkIfFamilyMemberAlreadyEnrolledInOfferedCourse(List<FamilyCourseRegistration> enrollmentsForOfferedCourse, FamilyMember familyMember) {
        for(FamilyCourseRegistration familyCourseRegistration : enrollmentsForOfferedCourse){
            if(familyCourseRegistration.getFamilyMember().getFamilyMemberId() == familyMember.getFamilyMemberId())
                throw new MemberAlreadyEnrolledInOfferedCourseException("Family Member is already enrolled in the offered course.");
        }
    }

    private boolean performEnrollment(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse) {
        double costOfOfferedCourse = getCostOfOfferedCourseForFamilyMember(offeredCourse, familyMember);

        FamilyCourseRegistration enrollment = FamilyCourseRegistration.builder()
                .cost(costOfOfferedCourse)
                .enrollmentActorId(actor.getFamilyMemberId())
                .enrollmentActor(actor.getName())
                .enrollmentDate(LocalDate.now())
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .build();

        // withdraw from FamilyCredits
        familyMember.getFamilyGroup().setCredits(familyMember.getFamilyGroup().getCredits() - costOfOfferedCourse);

        enrollment = familyCourseRegistrationRepository.save(enrollment);
        return enrollment.getFamilyCourseRegistrationId() != 0;
    }

    public double getCostOfOfferedCourseForFamilyMember(OfferedCourse offeredCourse, FamilyMember familyMember) {
        FeeType feeType = FeeType.NON_RESIDENT;
        if(familyMember.getCity().equals(offeredCourse.getFacility().getCity())) feeType = FeeType.RESIDENT;

        OfferedCourseFee offeredCourseFee = offeredCourseFeeRepository.findByOfferedCourse_OfferedCourseIdAndFeeType(offeredCourse.getOfferedCourseId(), feeType)
                .orElseThrow(() -> new OfferedCourseFeeNotFoundException("Offered Course Fee could not be found right now."));
        return offeredCourseFee.getCourseFee();
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
         familyManagementService.checkFamilyMemberValidity(actorMemberLoginId);
         FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(memberLoginId);

        FamilyCourseRegistration registration = familyCourseRegistrationRepository.findByOfferedCourse_OfferedCourseIdAndFamilyMember_MemberLoginIdAndIsWithdrawn(offeredCourseId, memberLoginId, false)
                .orElseThrow(() -> new RegistrationNotFoundException("No Registration found for the Offered Course and the Family Member."));

        registration.setWithdrawn(true);
        registration.setWithdrawnCredits(registration.getCost());
        familyMember.getFamilyGroup().setCredits(familyMember.getFamilyGroup().getCredits() + registration.getCost());

        familyCourseRegistrationRepository.save(registration);

        List<FamilyCourseWaitlist> waitlists = familyCourseWaitlistRepository.findAllByOfferedCourse_OfferedCourseIdAndIsWaitlisted(offeredCourseId, true);
        for (FamilyCourseWaitlist familyCourseWaitlist : waitlists){
            sendCourseAvailabilityNotificationViaEms(familyCourseWaitlist.getFamilyMember(), familyCourseWaitlist.getOfferedCourse());
        }

        return true;
    }

    private void sendCourseAvailabilityNotificationViaEms(FamilyMember familyMember, OfferedCourse offeredCourse) {
        log.info("Sending notification to {}", familyMember.getName());
        String offeredCourseEnrollmentLink = "http://localhost:8080/CanadaActiveLife/v1/courseRegistrations/enrollment";
        String message = "The OfferedCourse with ID: "+offeredCourse.getOfferedCourseId()+" is now available. To enroll in this course click on this link: "+offeredCourseEnrollmentLink;

        String url = "http://localhost:8082/api/ens/sms";
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerId("akshat11") // Admin User for ENS
                .phoneNumber(familyMember.getHomePhoneNumber())
                .message(message)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDTO> entity = new HttpEntity<>(customerDTO, headers);

        ResponseEntity<String> response =  restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        if(!Objects.equals(response.getBody(), "Message Sent Via SMS."))
            throw new FailedToSendNotificationException("Failed to send Offered Course Availability Notification.");

    }

    public boolean waitlistFamilyMember(String actorMemberLoginId, String familyMemberLoginId, int offeredCourseId) {
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(actorMemberLoginId);
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(familyMemberLoginId);
        OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(offeredCourseId);

        FamilyCourseWaitlist waitlist = FamilyCourseWaitlist.builder()
                .enrollmentActorId(actor.getFamilyMemberId())
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .build();

        waitlist = familyCourseWaitlistRepository.save(waitlist);
        return waitlist.getFamilyCourseWaitlistId() != 0;
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
