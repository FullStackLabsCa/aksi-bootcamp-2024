package org.reactivestax.canada_active_life.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.PendingLoginUUID;
import org.reactivestax.canada_active_life.domain.PendingSignUpUUID;
import org.reactivestax.canada_active_life.dto.CustomerDTO;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;
import org.reactivestax.canada_active_life.dto.UserVerificationDTO;
import org.reactivestax.canada_active_life.exception.*;
import org.reactivestax.canada_active_life.mapper.FamilyMemberMapper;
import org.reactivestax.canada_active_life.repo.FamilyGroupRepository;
import org.reactivestax.canada_active_life.repo.FamilyMemberRepository;
import org.reactivestax.canada_active_life.repo.PendingLoginUUIDRepository;
import org.reactivestax.canada_active_life.repo.PendingSignUpUUIDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FamilyManagementService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private PendingSignUpUUIDRepository pendingSignUpUUIDRepository;

    @Autowired
    private PendingLoginUUIDRepository pendingLoginUUIDRepository;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public boolean signUpNewFamilyMember(FamilyMemberDTO familyMemberDTO) {
        /**
         * createFamilyGroup - Done
         * createFamilyMember - with groupId created - isActive to false - Done
         * create UUIDToken and save this in the uuidTokenManagement table - Done
         * sendActivationLink with the UUIDToken - Done
         */
        FamilyGroup createdFamilyGroup = createFamilyGroup(familyMemberDTO.getFamilyPin());

        FamilyMember createdFamilyMember = createFamilyMember(familyMemberDTO, createdFamilyGroup);
        familyMemberRepository.save(createdFamilyMember);
        createdFamilyGroup.setCreatedBy(createdFamilyMember.getFamilyMemberId());
        familyGroupRepository.save(createdFamilyGroup);

        return sendActivationLink(createdFamilyMember);
    }

    public boolean sendActivationLink(FamilyMember createdFamilyMember) {
        UUID uuidToken = createUUIDTokenForSignUpActivation(createdFamilyMember);
        return sendActivationLinkViaEms(createdFamilyMember.getFamilyMemberId(), uuidToken, createdFamilyMember.getHomePhoneNumber());
    }

    private FamilyGroup createFamilyGroup(String familyPin) {
        return FamilyGroup.builder()
                .familyPin(familyPin)
                .build();
    }

    private FamilyMember createFamilyMember(FamilyMemberDTO familyMemberDTO, FamilyGroup createdFamilyGroup) {
        return FamilyMember.builder()
                .name(familyMemberDTO.getName())
                .dob(familyMemberDTO.getDob())
                .gender(familyMemberDTO.getGender())
                .emailAddress(familyMemberDTO.getEmailAddress())
                .streetNumber(familyMemberDTO.getStreetNumber())
                .streetName(familyMemberDTO.getStreetName())
                .city(familyMemberDTO.getCity())
                .province(familyMemberDTO.getProvince())
                .country(familyMemberDTO.getCountry())
                .homePhoneNumber(familyMemberDTO.getHomePhoneNumber())
                .businessPhoneNumber(familyMemberDTO.getBusinessPhoneNumber())
                .preferredContactMethod(familyMemberDTO.getPreferredContactMethod())
                .language(familyMemberDTO.getLanguage())
                .memberLoginId(familyMemberDTO.getMemberLoginId())
                .familyGroup(createdFamilyGroup)
                .build();
    }

    private UUID createUUIDTokenForSignUpActivation(FamilyMember createdFamilyMember) {
        UUID uuid  = UUID.randomUUID();
        PendingSignUpUUID uuidToken = PendingSignUpUUID.builder()
                .uuid(uuid)
                .familyMember(createdFamilyMember)
                .build();
        pendingSignUpUUIDRepository.save(uuidToken);
        return uuid;
    }

    private boolean sendActivationLinkViaEms(int familyMemberId, UUID uuidToken, String phoneNumber) {
        log.info("Sending Activation Link Via Ems... Family Member Id: {}, UUIDToken: {}", familyMemberId, uuidToken.toString());
        String activationLink = "http://localhost:8080/CanadaActiveLife/v1/activate-account?uuid="+uuidToken+"&familyMemberId="+familyMemberId;

        String url = "http://localhost:8082/api/ens/sms";
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerId("akshat11") // Admin User for ENS
                .phoneNumber(phoneNumber)
                .message(activationLink)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDTO> entity = new HttpEntity<>(customerDTO, headers);

        ResponseEntity<String> response =  restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        return Objects.equals(response.getBody(), "Message Sent Via SMS.");
    }

    public boolean activateNewSignUp(int familyMemberId, UUID uuid){
        /**
         * map the familyMember and the UUID in the uuidTokenTable Table - Done
         * once the match is found - get familyMember and set isActive true - Done
         * check the group as well, if inactive, set to active - Done
         * save familyMember - Done
         */
        pendingSignUpUUIDRepository.findTopByUuidAndFamilyMember_FamilyMemberIdAndCreationTimeStampAfterOrderByCreationTimeStampDesc(uuid, familyMemberId, LocalDateTime.now().minusHours(48))
                .orElseThrow(() -> new InvalidSignUpActivationLinkException("The Sign Up activation link is invalid. Please generate another one..."));
        FamilyMember familyMember = familyMemberRepository.findByFamilyMemberId(familyMemberId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family member was not found!"));
        familyMember.setActive(true);
        if(familyMember.getFamilyGroup().getStatus().equals("inactive"))
            familyMember.getFamilyGroup().setStatus("active");
        familyMemberRepository.save(familyMember);
        return true;
    }

    @Transactional
    public boolean addFamilyMember(FamilyMemberDTO familyMemberDTO, String memberLoginId) {
        /**
         * checkActorValidity() - Done
         * Get the familyGroupId for the actor. - Done
         * Create a familyMember with the familyMemberDTO and set the FamilyGroupId as above. - Done
         * familyManagementRepository.save(familyMember) - Done
         * Activation Link Same as SignUp - Done
         */
        FamilyMember actor = checkFamilyMemberValidity(memberLoginId);

        FamilyGroup familyGroupOfActor = actor.getFamilyGroup();
        FamilyMember createdFamilyMember = createFamilyMember(familyMemberDTO, familyGroupOfActor);
        familyMemberRepository.save(createdFamilyMember);

        return sendActivationLink(createdFamilyMember);
    }

    public FamilyMember checkFamilyMemberValidity(String memberLoginId) {
        return familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Unable to find the familyMember"));
    }

    public FamilyMemberDTO getFamilyMember(String memberLoginId) {
        /**
         * checkActorValidity() - Optional
         * familyManagementRepo.findById(familyMemberId)
         */
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family Member not found for the given Member Login Id."));

        return familyMemberMapper.toDto(familyMember);
    }

    public FamilyMemberDTO updateFamilyMemberInfo(FamilyMemberDTO familyMemberDTO, String memberLoginId) {
        /**
         * checkActorValidity() - Optional
         * checkIfFamilyMemberExist - Done
         * Update the changes in the familyMember extracted - Done
         */
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family Member Not Found."));

        familyMemberMapper.updateFamilyMemberFromDto(familyMemberDTO, familyMember);

        familyMemberRepository.save(familyMember);
        return familyMemberMapper.toDto(familyMember);
    }

    public boolean deactivateFamilyMember(String memberLoginId, String actorMemberLoginId) {
        /**
         * checkActorValidity() - Done
         * checkIfFamilyMemberExist() - Done
         * update the isActive field to inactive - Done
         * save(FamilyMember) - Done
         *
         * If the memberBeingDeactivated is the last member in the group, then deactivate the group - Optional
         */
        FamilyMember actor = checkFamilyMemberValidity(actorMemberLoginId);
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException(""));

        if(familyMember.getFamilyGroup().getCreatedBy() != actor.getFamilyMemberId())
            throw new ActorNotAuthorizedException("Actor is not authorized to deactivate any account in the group");

        familyMember.setActive(false);
        familyMemberRepository.save(familyMember);

        return true;
    }

    public UUID loginMember(UserLoginDTO userLoginDTO) {
        /**
         * Extract the memberLoginId and the FamilyPin from the UserLoginDTO
         * Verify MemberLoginId exists
         * Check verification status
         *      if not active - send activation link
         * Match the memberLoginId and FamilyPin
         *      if match - generate and save UUID and familyMemberId in DB and request for an OTP to be sent
         * Log in the LoginRequest Table
         */
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(userLoginDTO.getMemberLoginId())
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family Member not Found"));
        if(!familyMember.isActive()){
            UUID uuidTokenForSignUpActivation = createUUIDTokenForSignUpActivation(familyMember);
            sendActivationLinkViaEms(familyMember.getFamilyMemberId(), uuidTokenForSignUpActivation, familyMember.getHomePhoneNumber());
            throw new MemberNotActivatedException("Member has been sent an activation link. Please click on the link to activate the account.");
        }
        if(familyMember.getFamilyGroup().getFamilyPin().equals(userLoginDTO.getFamilyPin())){
            UUID uuidTokenForLogin = createUUIDTokenForLogin(familyMember);
            sendOTPViaEms(familyMember);
            return uuidTokenForLogin;
        }
        return null;
    }

    private void sendOTPViaEms(FamilyMember familyMember) {
        log.info("Sending OTP via EMS on preferred contact method to the member {}.", familyMember.getName());

        String url = "http://localhost:8082/api/otp/sms";
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerId("akshat11") // TODO replace with Customer Management API in EMS
                .phoneNumber(familyMember.getHomePhoneNumber())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDTO> entity = new HttpEntity<>(customerDTO, headers);

        ResponseEntity<String> response =  restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        if(!Objects.equals(response.getBody(), "OTP Sent Via SMS."))
            throw new FailedToSendOtpException("Login OTP failed to send....");
    }

    private UUID createUUIDTokenForLogin(FamilyMember createdFamilyMember) {
        UUID uuid  = UUID.randomUUID();
        PendingLoginUUID uuidToken = PendingLoginUUID.builder()
                .uuid(uuid)
                .familyMember(createdFamilyMember)
                .build();
        pendingLoginUUIDRepository.save(uuidToken);
        return uuid;
    }

    public boolean loginVerification(UserVerificationDTO userVerificationDTO, UUID uuid) {
        /**
         * Get the UUID from the header - Verify mapping for the UUID in loginTable and get FamilyMemberId for the UUID - Done
         *      If not found or UUID expired - Throw exception Session Expired Please try again - Done
         * Get the OTP entered by the user from the DTO and forward that along with the FamilyMemberId to the VerifyOTP EMS Service - Done
         * Wait for the response from EMS and return value based on the Verification - Done
         */
        PendingLoginUUID pendingLoginUUID =
                pendingLoginUUIDRepository.findTopByUuidAndCreationTimeStampAfterOrderByCreationTimeStampDesc(uuid, LocalDateTime.now().minusHours(2))
                    .orElseThrow(() -> new UUIDTokenExpiredException("Your Login UUID Token Expired. Please Generate a new one."));

        return verifyOtpViaEms(userVerificationDTO.getOtpEnteredByUser(), pendingLoginUUID.getFamilyMember().getFamilyMemberId());
    }

    private boolean verifyOtpViaEms(String otpEnteredByUser, int familyMemberId) {
        log.info("Sending OTP for Verification to EMS. OTP Entered: {} by user: {}", otpEnteredByUser, familyMemberId);

        String url = "http://localhost:8082/api/otp/verify";
        CustomerDTO customerDTO = CustomerDTO.builder()
                .customerId("akshat11") // TODO replace with Customer Management API in EMS
                .message(otpEnteredByUser)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDTO> entity = new HttpEntity<>(customerDTO, headers);

        ResponseEntity<String> response =  restTemplate.postForEntity(
                url,
                entity,
                String.class
        );

        return Objects.equals(response.getBody(), "Customer Verified");
    }
}
