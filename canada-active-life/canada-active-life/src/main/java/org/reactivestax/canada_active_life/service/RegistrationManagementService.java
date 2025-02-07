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
import org.reactivestax.canada_active_life.repo.UnconfirmedPaymentRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private UnconfirmedPaymentRegistrationRepository unconfirmedPaymentRegistrationRepository;

    @Autowired
    private RestTemplate restTemplate;

    public boolean enrollFamilyMemberInOfferedCourse(FamilyMemberCourseRegistrationDTO familyMemberCourseRegistrationDTO, String memberLoginId) {
        FamilyMember actor = familyManagementService.checkFamilyMemberValidity(memberLoginId);
        FamilyMember familyMember = familyManagementService.checkFamilyMemberValidity(familyMemberCourseRegistrationDTO.getFamilyMemberLoginId());
        if(!familyMember.isActive()) {
            familyManagementService.sendActivationLink(familyMember);
            throw new FamilyMemberNotActivatedException("Please verify family member using the activation link sent via sms...");
        }

        OfferedCourse offeredCourse = offeredCourseService.getOfferedCourseById(familyMemberCourseRegistrationDTO.getOfferedCourseId());
        if("CLOSED".equals(offeredCourse.getAvailableForEnrollment())) throw new OfferedCourseNotAvailableForEnrollmentException("Offered Course not available for enrollment.");
        int totalOfferedSeats = offeredCourse.getSeatsAvailable();

        List<FamilyCourseRegistration> enrollmentsForOfferedCourse = getEnrollmentsForOfferedCourse(offeredCourse);
        checkIfFamilyMemberAlreadyEnrolledInOfferedCourse(enrollmentsForOfferedCourse, familyMember);
        Optional<FamilyCourseWaitlist> familyCourseWaitlist = checkIfFamilyMemberInWaitlist(offeredCourse, familyMember);


        int seatsTaken = enrollmentsForOfferedCourse.size();

        if(seatsTaken < totalOfferedSeats){
            familyCourseWaitlist.ifPresent(courseWaitlist -> courseWaitlist.setWaitlisted(false));
            return performEnrollment(actor, familyMember, offeredCourse);
        } else {
            familyCourseWaitlist.ifPresent(value -> {throw new MemberAlreadyWaitlistedForOfferedCourseException("Family Member is already wailisted in the offered course.");});
            if(waitlistFamilyMember(actor.getFamilyMemberId(), familyMember, offeredCourse)) throw new FamilyMemberWaitlistedForOfferedCourse("Course Full! Family Member has been waitlisted for the offered course. You will get notification as spot comes available.");
        }
        return false;
    }

    public boolean holdPositionForFamilyMemberInOfferedCourse(int actorFamilyMemberId, FamilyMember familyMember, OfferedCourse offeredCourse, double cost){
        Optional<FamilyCourseWaitlist> familyCourseWaitlist = checkIfFamilyMemberInWaitlist(offeredCourse, familyMember);

        if(checkFamilyMemberAndOfferedCourseValidity(familyMember, offeredCourse)){
            familyCourseWaitlist.ifPresent(courseWaitlist -> courseWaitlist.setWaitlisted(false));
            return addToUnconfirmedPaymentRegistration(actorFamilyMemberId, familyMember, offeredCourse, cost);
        } else {
            familyCourseWaitlist.ifPresent(waitlist -> log.info("Family Member is already waitlisted in the offered course."));
            return  false;
        }
    }

    public boolean checkFamilyMemberAndOfferedCourseValidity(FamilyMember familyMember, OfferedCourse offeredCourse){
        if(!familyMember.isActive()) {
            familyManagementService.sendActivationLink(familyMember);
            log.info("Please verify family member using the activation link sent via sms...");
            return false;
        }

        if("CLOSED".equals(offeredCourse.getAvailableForEnrollment())) return false;
        int totalOfferedSeats = offeredCourse.getSeatsAvailable();

        List<FamilyCourseRegistration> enrollmentsForOfferedCourse = getEnrollmentsForOfferedCourse(offeredCourse);
        List<UnconfirmedPaymentRegistration> seatsHeldBecauseOfPendingPayment = getNumSeatsOfPendingPayment(offeredCourse);
        checkIfFamilyMemberAlreadyEnrolledInOfferedCourse(enrollmentsForOfferedCourse, familyMember);

        int seatsTaken = enrollmentsForOfferedCourse.size();
        int seatsOnHold = seatsHeldBecauseOfPendingPayment.size();

        return seatsTaken + seatsOnHold < totalOfferedSeats;
    }

    private List<UnconfirmedPaymentRegistration> getNumSeatsOfPendingPayment(OfferedCourse offeredCourse) {
        return unconfirmedPaymentRegistrationRepository.findAllByOfferedCourse_OfferedCourseIdAndCreationTimeStampAfter(offeredCourse.getOfferedCourseId(), LocalDateTime.now().minusMinutes(3));
    }

    private boolean addToUnconfirmedPaymentRegistration(int actorFamilyMemberId, FamilyMember familyMember, OfferedCourse offeredCourse, double cost) {
        UnconfirmedPaymentRegistration tempRegistration = UnconfirmedPaymentRegistration.builder()
                .enrollmentActorId(actorFamilyMemberId)
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .cost(cost)
                .build();
        unconfirmedPaymentRegistrationRepository.save(tempRegistration);
        return true;
    }

    public boolean confirmRegistrationAfterPayment(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse){
        return performEnrollment(actor, familyMember, offeredCourse) &&
        removeFromUnconfirmedPaymentRegistration(actor, familyMember, offeredCourse);
    }

    public List<FamilyCourseRegistration> getEnrollmentsForOfferedCourse(OfferedCourse offeredCourse) {
        return familyCourseRegistrationRepository.findAllByOfferedCourse_OfferedCourseIdAndIsWithdrawn(offeredCourse.getOfferedCourseId(), false);
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
        double costOfOfferedCourse = getCostOfOfferedCourse(offeredCourse, familyMember);

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

        familyCourseRegistrationRepository.save(enrollment);
        return enrollment.getFamilyCourseRegistrationId() != 0;
    }

    private boolean removeFromUnconfirmedPaymentRegistration(FamilyMember actor, FamilyMember familyMember, OfferedCourse offeredCourse) {
        unconfirmedPaymentRegistrationRepository.deleteAllByEnrollmentActorIdAndFamilyMember_FamilyMemberIdAndOfferedCourse_OfferedCourseId(actor.getFamilyMemberId(), familyMember.getFamilyMemberId(), offeredCourse.getOfferedCourseId());
        return true;
    }

    public double getCostOfOfferedCourse(OfferedCourse offeredCourse, FamilyMember familyMember) {
        FeeType feeType = FeeType.NON_RESIDENT;
        if(familyMember.getCity().equals(offeredCourse.getFacility().getCity())) feeType = FeeType.RESIDENT;

        OfferedCourseFee offeredCourseFee = offeredCourseFeeRepository.findByOfferedCourse_OfferedCourseIdAndFeeType(offeredCourse.getOfferedCourseId(), feeType)
                .orElseThrow(() -> new OfferedCourseFeeNotFoundException("Offered Course Fee could not be found right now."));
        return offeredCourseFee.getCourseFee();
    }

    private boolean waitlistFamilyMember(int actorFamilyMemberId, FamilyMember familyMember, OfferedCourse offeredCourse) {
        FamilyCourseWaitlist waitlist = FamilyCourseWaitlist.builder()
                .enrollmentActorId(actorFamilyMemberId)
                .offeredCourse(offeredCourse)
                .familyMember(familyMember)
                .build();

        familyCourseWaitlistRepository.save(waitlist);
        return waitlist.getFamilyCourseWaitlistId() != 0;
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
