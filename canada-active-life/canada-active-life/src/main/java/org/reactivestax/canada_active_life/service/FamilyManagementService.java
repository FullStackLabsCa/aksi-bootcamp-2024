package org.reactivestax.canada_active_life.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.reactivestax.canada_active_life.domain.FamilyGroup;
import org.reactivestax.canada_active_life.domain.FamilyMember;
import org.reactivestax.canada_active_life.domain.PendingLoginUUID;
import org.reactivestax.canada_active_life.domain.PendingSignUpUUID;
import org.reactivestax.canada_active_life.dto.FamilyMemberDTO;
import org.reactivestax.canada_active_life.dto.UserLoginDTO;
import org.reactivestax.canada_active_life.dto.UserVerificationDTO;
import org.reactivestax.canada_active_life.exception.ActorNotAuthorizedException;
import org.reactivestax.canada_active_life.exception.FamilyMemberNotFoundException;
import org.reactivestax.canada_active_life.exception.MemberNotActivatedException;
import org.reactivestax.canada_active_life.mapper.FamilyMemberMapper;
import org.reactivestax.canada_active_life.repo.FamilyGroupRepository;
import org.reactivestax.canada_active_life.repo.FamilyMemberRepository;
import org.reactivestax.canada_active_life.repo.PendingLoginUUIDRepository;
import org.reactivestax.canada_active_life.repo.PendingSignUpUUIDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Transactional
    public boolean signUpNewFamilyMember(FamilyMemberDTO familyMemberDTO) {
        /**
         * createFamilyGroup - Done
         * createFamilyMember - with groupId created - isActive to false - Done
         * create UUIDToken and save this in the uuidTokenManagement table - Done
         * sendActivationLink with the UUIDToken - TODO
         */
        FamilyGroup createdFamilyGroup = createFamilyGroup(familyMemberDTO.getFamilyPin());

        FamilyMember createdFamilyMember = createFamilyMember(familyMemberDTO, createdFamilyGroup);
        createdFamilyGroup.setGroupOwner(createdFamilyMember);
        familyMemberRepository.save(createdFamilyMember);

        UUID uuidToken = createUUIDTokenForSignUpActivation(createdFamilyMember);
        return sendActivationLinkViaEms(createdFamilyMember.getFamilyMemberId(), uuidToken);
    }

    private FamilyGroup createFamilyGroup(String familyPin) {
        FamilyGroup familyGroup = FamilyGroup.builder()
                .familyPin(familyPin)
                .build();
        return familyGroupRepository.save(familyGroup);
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

    private boolean sendActivationLinkViaEms(int familyMemberId, UUID uuidToken) {
        log.info("Sending Activation Link Via Ems..." + familyMemberId + " " + uuidToken.toString());
        return true;
    }

    public boolean activateNewSignUp(int familyMemberId, UUID uuid){
        /**
         * map the familyMember and the UUID in the uuidTokenTable Table - TODO
         * once the match is found - get familyMember and set isActive true - Done
         * check the group as well, if inactive, set to active - Done
         * save familyMember - Done
         */
        FamilyMember familyMember = familyMemberRepository.findByFamilyMemberId(familyMemberId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Family member was not found!"));
        familyMember.setActive(true);
        if(familyMember.getFamilyGroup().getStatus().equals("inactive"))
            familyMember.getFamilyGroup().setStatus("active");
        familyMemberRepository.save(familyMember);
        return true;
    }

    public boolean addFamilyMember(FamilyMemberDTO familyMemberDTO, String memberLoginId) {
        /**
         * checkActorValidity() - Done
         * Get the familyGroupId for the actor. - Done
         * Create a familyMember with the familyMemberDTO and set the FamilyGroupId as above. - Done
         * familyManagementRepository.save(familyMember) - Done
         *
         * Activation Link Same as SignUp - TODO
         */
        FamilyMember actor = checkActorValidity(memberLoginId);

        FamilyGroup familyGroupOfActor = actor.getFamilyGroup();
        FamilyMember createdFamilyMember = createFamilyMember(familyMemberDTO, familyGroupOfActor);
        familyMemberRepository.save(createdFamilyMember);

        UUID uuidToken = createUUIDTokenForSignUpActivation(createdFamilyMember);
        return sendActivationLinkViaEms(createdFamilyMember.getFamilyMemberId(), uuidToken);
    }

    private FamilyMember checkActorValidity(String memberLoginId) {
        return familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException("Unable to find the actor of the request."));
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
        FamilyMember actor = checkActorValidity(actorMemberLoginId);
        FamilyMember familyMember = familyMemberRepository.findByMemberLoginId(memberLoginId)
                .orElseThrow(() -> new FamilyMemberNotFoundException(""));

        if(familyMember.getFamilyGroup().getGroupOwner().getFamilyMemberId() != actor.getFamilyMemberId())
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
            sendActivationLinkViaEms(familyMember.getFamilyMemberId(), uuidTokenForSignUpActivation);
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
        log.info("Sending OTP via EMS on preferred contact method to the member.");
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

    public boolean loginVerification(UserVerificationDTO userVerificationDTO) {
        /**
         * Get the UUID from the header - Verify mapping for the UUID in loginTable and get FamilyMemberId for the UUID
         *      If not found or UUID expired - Throw exception Session Expired Please try again
         * Get the OTP entered by the user from the DTO and forward that along with the FamilyMemberId to the VerifyOTP EMS Service
         * Wait for the response from EMS and return value based on the Verification
         */
        return false;
    }
}
